package mywork.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Route
import finalServer.Server.system
import mywork.db.DBService
import mywork.models.MyWorkContext
import mywork.schema.GQSchema
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GraphQLHttp {

  import system.dispatcher

  private val dataLayer = DBService.createDatabase

  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {

    val JsObject(fields) = requestJSON
    val JsString(query) = fields("query")

    QueryParser.parse(query) match {
      case Success(queryAst) =>
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }
        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }
        complete(executeGraphQLQuery(queryAst, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }

  }

  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext): Future[(StatusCode with Serializable, JsValue)] = {
    Executor.execute(
      schema = GQSchema.SchemaDefinition,
      queryAst = query, // 11
      userContext = MyWorkContext(dataLayer), // 12  is a context object mentioned in the section above. In our example you can see that the context is built with the DAO object within.
      variables = vars, // 13
      operationName = operation // 14
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }
}
