package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/** Note - This Simulation is not in working condition */
/* Define the simulation class */
class SampleSimulation extends Simulation {

  /* Define the HTTP protocol configuration */
  val httpProtocol = http
    .baseUrl("http://localhost:8080") /* Base URL for all requests */
    .acceptHeader("*/*") /* Accept header for all requests */
    .doNotTrackHeader("1") /* Do Not Track header */
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0") /* User-Agent header */
    .disableWarmUp /* Disable warm-up */
    .disableCaching /* Disable caching */

  /* Define the GET scenario */
  val getScenario = scenario("BasicSimulation - GET")
    .exec(
      http("GET request") /* Name of the request */
        .get("/") /* GET request to the base URL */
        .check(status.is(200)) /* Check if the response status is 200 */
    )

  /* Define the POST scenario */
  val postScenario = scenario("BasicSimulation - POST")
    .exec(
      http("POST request") /* Name of the request */
        .post("https://reqres.in/api/register") /* POST request to the specified URL */
        .body(StringBody("""{ "email": "eve.holt@reqres.in", "password": "pistol" }""")) /* Request body */
        .asJson /* Specify that the body is in JSON format */
        .check(
          status.is(200), /* Check if the response status is 200 */
          jsonPath("$.id").exists, /* Check if the JSON response contains an "id" field */
          jsonPath("$.id").find.is("4") /* Check if the "id" field value is "4" */
        )
    )

  /* Set up the simulation with the defined scenarios */
  setUp(
    getScenario.inject(atOnceUsers(1)), /* Inject one user for the GET scenario */
    postScenario.inject(atOnceUsers(1)) /* Inject one user for the POST scenario */
  ).protocols(httpProtocol) /* Use the defined HTTP protocol */
}