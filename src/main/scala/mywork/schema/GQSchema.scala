package mywork.schema

//import java.util.UUID

import mywork.models.{Employee, MyWorkContext}
//import sangria.schema
import sangria.schema.{Field, ListType, ObjectType, Schema, fields}
//import sangria.validation.ValueCoercionViolation

object GQSchema {
  /*
    case object UUIDCoercionViolation extends ValueCoercionViolation("UUID type of value expected")

    implicit val UUIDType: ScalarType[UUID] = ScalarType[UUID]("Uuid", coerceOutput = schema.valueOutput, coerceUserInput = {
      case s: String => Right(UUID.fromString(s))
      case _ => Left(UUIDCoercionViolation)
    }, coerceInput = {
      case sangria.ast.StringValue(s, _, _, _, _) => Right(UUID.fromString(s))
      case _ => Left(UUIDCoercionViolation)
    })*/

  import sangria.macros.derive._

  implicit val EmployeeType: ObjectType[Unit, Employee] = deriveObjectType[Unit, Employee]()

  val QueryType: ObjectType[MyWorkContext, Unit] = ObjectType(
    "Query",
    fields[MyWorkContext, Unit](
      Field("allEmployees", ListType(EmployeeType), resolve = c => c.ctx.dataLayer.allEmployees)
    )
  )

  val SchemaDefinition: Schema[MyWorkContext, Unit] = Schema(QueryType)
}
