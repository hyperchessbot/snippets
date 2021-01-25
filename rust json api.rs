pub async fn get_payment(&self, id: &String) -> Payment {
            let http = reqwest::Client::new();
            let res = http.patch(&format!("https://api.mollie.com/v2/payments/{}", id))
                .header("Authorization", "Bearer test_xxxx")
                .send()
                .await.unwrap();

            let payment: Payment = serde_json::from_str(&res.text().await.unwrap()).unwrap();
            payment
        }

let json : serde_json::Value = serde_json::from_str(&res.text().await.unwrap()).unwrap();

*payment_id = str::replace(&json["id"].to_string(), "\"", "");
