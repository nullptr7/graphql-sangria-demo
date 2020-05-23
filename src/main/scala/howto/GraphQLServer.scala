package howto

import akka.http.scaladsl.server.Route
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import sangria.ast.Document
import sangria.execution._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCode
import models.MyContext
import sangria.marshalling.sprayJson._

/*
Time to implement the GraphQL Server. This object will be in the second layer of architecture, just after HTTP server.
Proper HTTP request will be converted into JSON object and sent to this server.
GraphQL Server will parse that JSON as GraphQL query, execute it and through HTTP layer send response back to the client.
It will also catch GraphQL parsing errors and convert those into the proper HTTP responses.
 */
object GraphQLServer {

  // 1 We need access to the database, so it’s the step where such connection is created.
  private val dao: DAO = DBSchema.createDatabase

  // 2 endpoint responds with Route type.
  // It will be used directly in the routing of HTTP server.
  // It expects JSON object as parameter.
  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {

    // 3 Main JSON Object is extracted from the root object and it consists three children.
    // The expected structure you can see in the following fragment
    /*
     {
        query: {},
        variables: {},
        operationName: ""
      }

      query is a query itself, variables is additional data for that query.
      In GraphQL you can send the query and arguments separately.
      You can also set name for the query, it’s what the third object is for.
      Imagine that query is like a function, usually you’re using anonymous functions,
      but for logging or other purposes you could add names. It’s send as operationName.
     */
    val JsObject(fields) = requestJSON

    // 4  We’re extracting query from request at this point.
    val JsString(query) = fields("query")

    // 5 When we have the query, we have to parse it
    // function we can use in this case. When it fails,
    // the server will respond with status 400 and error description in the body of response.
    // After successful parsing, we’re also trying to extract the other two keys operationName & variables
    QueryParser.parse(query) match {
      case Success(queryAst) =>
        // 6 Extracting operation
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }

        // 7 Extracting variable
        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }
        // 8 At the end all those three objects are passed to the execution function
        complete(executeGraphQLQuery(queryAst, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }

  }

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext): Future[(StatusCode with Serializable, JsValue)] = {
    // 9 Executor.execute is the most important call in this class because it’s the point where the query is executed.
    // If the executor responds with success, the result is sent back to the client.
    // In all other cases, the server will respond with status code 4xx and some kind of explanation of what was wrong with the query.
    // The Executor needs some data to fulfill the request. Three of them are
    Executor.execute(
      // 10  is the last object we have to explain here. It contains our Schema - what we are able to query for.
      // It also interprets how data is fetched and which data source it could use (i.e. one or more databases,
      // REST call to the other server...).
      // In short our SchemaDefinition file defines what we want to expose.
      // There are defined types (from GraphQL point of view) and shape of the schema a client is able to query for.
      // Because this file is still missing we will create it in the next step.
      schema = GraphQLSchema.SchemaDefinition,
      queryAst = query, // 11
      userContext = MyContext(dao), // 12  is a context object mentioned in the section above. In our example you can see that the context is built with the DAO object within.
      variables = vars, // 13
      operationName = operation // 14
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }
}
