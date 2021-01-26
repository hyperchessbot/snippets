use std::cell::RefCell;
use std::collections::VecDeque;
use std::sync::mpsc::{channel, Receiver, Sender};
use std::sync::{Arc, Mutex, RwLock};
use std::thread;
use std::time::Duration;

use actix::Addr;
use chrono::naive::NaiveDateTime;
use log::{Level, Metadata, Record};
use serde::Serialize;
use serde_json;

use crate::all::*;
use crate::api::logger::{LoggerEvent, LoggerWs};

#[derive(Serialize)]
pub enum WebLevel {
    Error,
    Warn,
    Info,
    Debug,
    Trace,
}

//#[derive(Sync)]
#[derive(Serialize)]
pub struct WebLog {
    time: NaiveDateTime,
    level: WebLevel,
    args: String,

    file: Option<&'static str>,
    line: Option<u32>,

    module: Option<&'static str>,
}

impl WebLog {
    pub fn new(record: &Record) -> Self {
        let time = chrono::Utc::now().naive_local();
        let level = match record.metadata().level() {
            Level::Error => WebLevel::Error,
            Level::Warn => WebLevel::Warn,
            Level::Info => WebLevel::Info,
            Level::Debug => WebLevel::Debug,
            Level::Trace => WebLevel::Trace,
        };
        let args = format!("{}", record.args());
        let file = record.file_static();
        let line = record.line();
        let module = record.module_path_static();

        Self {
            time,
            level,
            args,
            file,
            line,
            module,
        }
    }
}

pub struct WebLogger {
    sender: Arc<Mutex<Sender<WebLog>>>,
}

impl WebLogger {
    thread_local! {
        static SENDER: RefCell<Option<Sender<WebLog>>> = RefCell::new(None);
    }
}

impl log::Log for WebLogger {
    fn enabled(&self, metadata: &Metadata) -> bool {
        metadata.level() <= Level::Info
    }

    fn log(&self, record: &Record) {
        if record.metadata().level() != Level::Trace {
            Self::SENDER.with(|x| {
                let mut x = x.borrow_mut();
                if let None = *x {
                    match self.sender.lock() {
                        Ok(sender) => {
                            *x = Some(sender.clone());
                        }
                        Err(poisoned) => println!("web socket poisoned: {:?}", poisoned),
                    }
                }
                if let Some(sender) = &*x {
                    if let Err(err) = sender.send(WebLog::new(record)) {
                        //println!("error sending log: {:?}", err);
                    }
                }
            });
        }
    }

    fn flush(&self) {}
}

impl WebLogger {
    pub fn new(sender: Sender<WebLog>) -> Self {
        let sender = Arc::new(Mutex::new(sender));
        Self { sender }
    }
}

#[derive(Clone)]
pub struct WebLoggerSocket {
    clients: Arc<Mutex<VecDeque<Addr<LoggerWs>>>>,
    running: Arc<RwLock<bool>>,
    handle: Arc<Mutex<Option<thread::JoinHandle<()>>>>,
}

impl WebLoggerSocket {
    pub fn new() -> (Self, Sender<WebLog>) {
        let (tx, rx): (Sender<WebLog>, Receiver<WebLog>) = channel();
        let newclients = Arc::new(Mutex::new(VecDeque::new()));
        let t_newclients = newclients.clone();

        let running = Arc::new(RwLock::new(true));
        let t_running = running.clone();
        let handle = thread::spawn(move || -> () {
            let mut buffer: VecDeque<WebLog> = VecDeque::with_capacity(100);
            let mut clients: Vec<Addr<LoggerWs>> = Vec::with_capacity(10);
            let timeout = Duration::from_millis(300);
            loop {
                match t_newclients.lock() {
                    Ok(mut foundclients) => {
                        let mut foundclients: Vec<Addr<LoggerWs>> = foundclients.drain(..).collect();
                        //println!("new clients: {:?}", foundclients.len());
                        for client in &foundclients {
                            if client.connected() {
                                for message in &buffer {
                                    let message = serde_json::to_string(&message).unwrap();
                                    client.do_send(LoggerEvent(message.clone()));
                                }
                            }
                        }
                        clients.append(&mut foundclients);
                    }
                    Err(_) => println!("error locking clients!"),
                }

                match rx.recv_timeout(timeout) {
                    Ok(message) => {
                        let messagestr = serde_json::to_string(&message).unwrap();
                        for client in &clients {
                            if client.connected() {
                                client.do_send(LoggerEvent(messagestr.clone()));
                            }
                        }

                        buffer.push_back(message);
                        if buffer.len() > 98 {
                            buffer.drain(0..buffer.len() - 98);
                        }
                    }
                    Err(_) => {}
                };
                //println!("got message! {}", message);
                clients.retain(|client| client.connected());

                let running = match t_running.read() {
                    Ok(running) => *running,
                    Err(_) => false,
                };

                if !running {
                    debug!("logger exiting!");
                    break;
                }
            }
        });
        let handle = Arc::new(Mutex::new(Some(handle)));
        (
            WebLoggerSocket {
                clients: newclients,
                running,
                handle,
            },
            tx,
        )
    }

    pub fn push_client(&self, actor: Addr<LoggerWs>) -> Result<()> {
        match self.clients.lock() {
            Ok(mut clients) => {
                clients.push_back(actor);
                Ok(())
            }
            Err(_) => Err("error locking clients".into()),
        }
    }

    pub fn stop(&self) {
        *self.running.write().unwrap() = false;
        self.handle.lock().unwrap().take().unwrap().join().unwrap();
    }
}
