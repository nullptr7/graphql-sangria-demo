package howto

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Await
import scala.language.postfixOps

object Server extends App {

  //2
  val PORT = 8080

  implicit val actorSystem: ActorSystem = ActorSystem("graphql-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import actorSystem.dispatcher
  import scala.concurrent.duration._

  scala.sys.addShutdownHook(() -> shutdown())

  //3
  private val route: Route = {
    //complete("Hello GraphQL Scala!!!")
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


  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }
}
