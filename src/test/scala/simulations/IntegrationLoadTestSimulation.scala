package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class IntegrationLoadTestSimulation extends Simulation {

  val protocols = http
    .disableCaching // sending no-cache header
    .disableWarmUp // disable initial warm up
    .baseUrl("http://localhost:8080") // setting the global base url
    .contentTypeHeader("application/json") // setting the global content-type header
    .acceptHeader("application/json") // setting the global accept header

  val scn = scenario("sample scenario")
    .exec(http("fetch all the users")
      .get("/api/users") // sending GET request
      .check(
        status.is(200), // checking whether the http status code is 200
        jsonPath("$.data[*].id").exists, // extracting the id from response body
        jsonPath("$.data[*].id").findRandom.saveAs("randomId") // fetching random id and saving to session in key 'randomId'
      )
    )
    .exitHereIfFailed // to stop further execution if any failure occurred in the previous exec
    .exec(http("get info for user id ${randomId}") // fetch the 'randomId' from user session
      .get("/api/users/${randomId}") // fetch the user information
      .check(
        status.is(200), // checking whether the http status code is 200
        status.not(400), // checking whether the http status code is not 400
        status.in(200, 201), // checking whether the http status code is in (200, 201)
        jsonPath("$.data.id").ofType[Int].is(session => session("randomId").as[Int]),
        jsonPath("$.data.first_name").transform(str => str.trim).saveAs("first_name"), // extract 'first_name' from response, transform the value by trimming and save it to session in key 'first_name'
        jsonPath("$.data.last_name").transform((str, session) => str.trim + " " + session("first_name").as[String]).saveAs("full_name")
      ))
    .exitHereIfFailed // to stop further execution if any failure occurred in the previous exec
    .exec(http("update user id ${randomId}")
      .put("/api/users/${randomId}") // sending PUT request
      .body(StringBody("""{ "name": "gatling", "job": "gatling" }"""))
      .asJson // setting both content-type and accept to 'application/json' overridding the global setting
      .check(
        status.is(200), // checking whether the http status code is 200
        jsonPath("$.name").is("gatling"), // checking whether the extracted 'name' field from response body is 'gatling'
        jsonPath("$.job").is("gatling"), // checking whether the extracted 'job' field from response body is 'gatling'
        jsonPath("$.updatedAt").exists // checking whether the extracted 'updatedAt' field exists
      )
    )
    .exitHereIfFailed // to stop further execution if any failure occurred in the previous exec
    .exec(http("delete user ${randomId}")
      .delete("/api/users/${randomId}") // sending DELETE request
      .check(
        status.is(204) // checking whether the http status code is 204
      )
    )

  // running the entire sequence of APIs 1 time
  setUp(scn.inject(atOnceUsers(1))).protocols(protocols)
}