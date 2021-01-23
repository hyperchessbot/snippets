use std::time::Duration;

use actix_web::{get, App, HttpServer};

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    tokio::spawn(async {
        actix_web::rt::time::sleep(Duration::from_secs(1)).await;
        println!("tokio spawn");
    });

    actix_web::rt::spawn(async {
        tokio::time::sleep(Duration::from_secs(2)).await;
        println!("actix web spawn");
    });

    HttpServer::new(move || App::new().service(index))
        .bind("127.0.0.1:8000")?
        .run()
        .await
}

#[get("/")]
async fn index() -> &'static str {
    "Hello World!"
}

/////////////////////////////////////////////////////////

use std::time::Duration;

use actix_web::{get, App, HttpServer};

fn main() -> std::io::Result<()> {
    let tokio_rt = tokio::runtime::Builder::new_current_thread()
        .enable_all()
        .build()?;

    tokio_rt.spawn(async {
        actix_web::rt::time::sleep(Duration::from_secs(1)).await;
        println!("tokio spawn");
    });

    let server = actix_web::rt::System::new("abc").block_on(async {
       let server = HttpServer::new(|| App::new().service(index))
            .disable_signals()
            .bind("127.0.0.1:8000")?
            .run();
        Ok::<_, std::io::Error>(server)
    })?;

    tokio_rt.block_on(async move {
        let _ = tokio::signal::ctrl_c().await;
        let _ = server.stop(true).await;
        Ok(())
    })
}

#[get("/")]
async fn index() -> &'static str {
    "Hello World!"
}
