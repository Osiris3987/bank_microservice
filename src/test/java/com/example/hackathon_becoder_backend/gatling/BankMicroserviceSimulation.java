package com.example.hackathon_becoder_backend.gatling;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.StringBody;

public class BankMicroserviceSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = HttpDsl
            .http
            .baseUrl("http://localhost:8080");

    private ChainBuilder postTransactions = CoreDsl.exec(
            HttpDsl
                    .http("creating transactions")
                    .post("/api/v1/transactions")
                    .header("Content-Type", "application/json")
                    .queryParam("clientId", "f0caf844-5a61-43a7-b1c2-e66971f5e08a")
                    .queryParam("legalEntityId", "b3ec6a4c-6245-419d-b884-024a69fea3eb")
                    .body(StringBody("{ \"type\": \"REFILL\", \"amount\": 1000 }"))
                    .check(HttpDsl.status().is(200))
    );

    private ChainBuilder getTransactionsByLegalEntityId = CoreDsl.exec(
            HttpDsl
                    .http("getting transactions by legal entity id")
                    .get("/api/v1/legalEntity/b3ec6a4c-6245-419d-b884-024a69fea3eb/transactions")
                    .check(HttpDsl.status().is(200))
    );

    private ScenarioBuilder postAndGetTransactionsByLegalEntityId = CoreDsl
            .scenario("post and get transactions")
            .exec(postTransactions);

    public BankMicroserviceSimulation() {
        this.setUp(
                postAndGetTransactionsByLegalEntityId.injectOpen(
                        CoreDsl.constantUsersPerSec(50).during(Duration.ofSeconds(50))
                )
        ).protocols(httpProtocol);
    }

}