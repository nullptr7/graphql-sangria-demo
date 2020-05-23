package mywork1

import sangria.schema.{Field, ListType, ObjectType}
import mods._
// #
import sangria.schema._

object GraphQLSchema {

  import sangria.macros.derive._

  implicit val EmployeeType: ObjectType[Unit, Employee] = deriveObjectType[Unit, Employee]()

  val QueryType: ObjectType[MyWorkContext, Unit] = ObjectType(
    "Query",
    fields[MyWorkContext, Unit](
      Field("allEmployees", ListType(EmployeeType), resolve = c => c.ctx.dao.allEmployees)
    )
  )

  // 3
  val SchemaDefinition: Schema[MyWorkContext, Unit] = Schema(QueryType)


}
