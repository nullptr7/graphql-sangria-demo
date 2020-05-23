package mywork1

import akka.http.scaladsl.server.Directives.{as, entity, getFromResource, path, post}
import spray.json.JsValue


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Await
import scala.language.postfixOps

object Server extends App {

  val PORT = 8080

  implicit val actorSystem: ActorSystem = ActorSystem("graphql-server")

  import actorSystem.dispatcher
  import scala.concurrent.duration._

  scala.sys.addShutdownHook(() -> shutdown())

  private val route: Route = {
    (post & path("graphql")) {
      entity(as[JsValue]) { requestJson =>
        GraphQLServer.endpoint(requestJson)
      }
    } ~ {
      getFromResource("assets/playground.html")
    }
  }

  Http().bindAndHandle(route, "0.0.0.0", PORT)
  println(s"open a browser with URL: http://localhost:$PORT")
  FetchAndSaveEmployeeService.main(null) // Different mode of execution hence will need to inject here to be a same JVM context


  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }
}
