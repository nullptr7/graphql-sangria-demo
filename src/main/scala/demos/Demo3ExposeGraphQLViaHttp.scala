package demos

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import common.GraphQLRoutes
import io.circe.Json
import model.{Book, Employee}
import sangria.execution._
import sangria.macros.derive._
import sangria.marshalling.circe._
import sangria.schema._

import scala.concurrent.Future
import scala.language.postfixOps

/** Let's expose our GraphQL schema via HTTP API */
object Demo3ExposeGraphQLViaHttp extends App {

  // STEP: Define some data

  val books = List(
    Book("1", "Harry Potter and the Philosopher's Stone", "J. K. Rowling"),
    Book("2", "A Game of Thrones", "George R. R. Martin"))

  val employees = List(
    Employee("1", "Foo", 123.00, "Manager", true),
    Employee("2", "Too", 123.00, "Software Engineer", true),
    Employee("3", "Goo", 123.00, "Trainee", true),
    Employee("4", "Loo", 123.00, "CEO", true),
  )
  // STEP: Define GraphQL Types & Schema

  val BookType: ObjectType[Unit, Book] = deriveObjectType[Unit, Book]()

  val employeeType: ObjectType[Unit, Employee] = deriveObjectType[Unit, Employee]()

  val QueryType: ObjectType[Unit, Unit] = ObjectType("Query", fields[Unit, Unit](
    Field("books", ListType(BookType), resolve = _ => books)))

  val queryTypeForEmployee: ObjectType[Unit, Unit] = ObjectType("Query", fields[Unit, Unit](
    Field("employees", ListType(employeeType), resolve = _ => employees)))

  val schema = Schema(QueryType)

  val employeeSchema = Schema(queryTypeForEmployee)

  // STEP: Create akka-http server and expose GraphQL route

  implicit val system = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  // NEW: define GraphQL route
  val route: Route = GraphQLRoutes.route { (query, operationName, variables, _, _) =>
    // NEW: execute GraphQL query coming from HTTP request
    val value: Future[Json] = Executor.execute(schema, query, variables = variables, operationName = operationName)
    value
  }

  val routeEmployee: Route = GraphQLRoutes.route { (query, operationName, variables, _, _) =>
    // NEW: execute GraphQL query coming from HTTP request
    Executor.execute(employeeSchema, query, variables = variables, operationName = operationName)
  }

  // NEW: start an HTTP server and serve the GraphQL route
  Http().bindAndHandle(routeEmployee, "0.0.0.0", 8080)
}
