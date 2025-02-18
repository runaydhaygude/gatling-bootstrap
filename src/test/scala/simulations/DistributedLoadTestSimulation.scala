package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class DistributedLoadTestSimulation extends Simulation {

  val protocols = http
    .disableCaching // disable caching of response
    .disableWarmUp // disable initial warmup before sending actual requests
    .baseUrls("http://localhost:8080", "http://localhost:8080") // setting base URLs
    .contentTypeHeader("application/json") // setting content-type header
    .acceptHeader("application/json") // setting accept header

  val scn = scenario("sample scenario")
    .exec(http("fetch all the users")
      .get("/api/users") // making a GET request to fetch all users
      .check(
        status.is(200), // assert http status code is 200
        jsonPath("$.data[*].id").exists
        // extract list of ids from response and check if they exist
      )
    )

  // creating an user injection profile to execute the scenario at a
  // constant rate of 2 users per second over a duration of 5 seconds
  // with http global configurations as declared.
  setUp(scn.inject(constantUsersPerSec(2) during (5 seconds))).protocols(protocols)
}
