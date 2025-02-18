package simulations


import io.gatling.core.scenario.Simulation;
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class StressTestSimulation extends Simulation {

    val protocols = http
            .disableCaching
            .disableWarmUp
            .baseUrl("http://localhost:8080")
            .contentTypeHeader("application/json")
            .acceptHeader("application/json")

    val scn = scenario("fetching all derivatives")
            .exec(http("fetch all derivatives")
                    .get("/api/v3/derivatives/exchanges")
                    .check(
                            status.is(200),
                            jsonPath("$[*].id").exists
                    )
            )

    setUp(
            scn.inject(constantUsersPerSec(5) during (10 seconds))
            ).protocols(protocols)
    .assertions(global.failedRequests.count.is(0))
}
