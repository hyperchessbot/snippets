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
