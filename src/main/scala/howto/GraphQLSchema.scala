package howto

import sangria.schema.{Field, ListType, ObjectType}
import models._
// #
import sangria.schema._

object GraphQLSchema {
  /* 1.
      Is a definition of ObjectType for our Link class. First (String) argument defines the name in the schema.
      If you want it could differ from name of case class.
      In fields you have to define all those fields/functions you want to expose.
      Every field has to contain a resolve function which tells Sangria how to retrieve data for this field.
      As you can see there is also an explicitly defined type for that field.
      Manual mapping could be boring in cases where you have to map many case classes.
      To avoid boilerplate code you can use the provided macro

      // import sangria.macros.derive._

      implicit val LinkType = deriveObjectType[Unit, Link]()
   */
  val LinkType: ObjectType[Unit, Link] = ObjectType[Unit, Link](
    name = "Link",
    fields[Unit, Link](
      Field(
        name = "id",
        fieldType = IntType,
        resolve = _.value.id),
      // Below are just shorthand version
      Field("url", StringType, resolve = _.value.url),
      Field("description", StringType, resolve = _.value.description)
    )
  )

  /*
      2.
      val QueryType is a top level object of our schema.
      As you can see, the top level object has a name Query and
      it (along with nested objects) will be visible in the graphiql console that we will include later in this chapter.
      In fields definition I’ve added only one Field at the moment
   */
  val QueryType: ObjectType[MyContext, Unit] = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field("allLinks", ListType(LinkType), resolve = c => c.ctx.dao.allLinks)
    )
  )

  // 3
  val SchemaDefinition: Schema[MyContext, Unit] = Schema(QueryType)

}
