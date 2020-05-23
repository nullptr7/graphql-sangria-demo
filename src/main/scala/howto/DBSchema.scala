package howto

import models.Link
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object DBSchema {

  // 1. Defines mapping to database table
  class LinksTable(tag: Tag) extends Table[Link](tag, "LINK") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def url = column[String]("URL")

    def description = column[String]("DESCRIPTION")

    override def * : ProvenShape[Link] = (id, url, description).mapTo[Link]
  }

  // Responsible for creating schema and adding three entities via databaseSetup variable
  def createDatabase: DAO = {
    val db: H2Profile.backend.Database = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)

    new DAO(db)

  }

  // 2. Gives us a helper, that we will use to access data from the table
  val Links = TableQuery[LinksTable]

  private val databaseSetup =
    DBIO.seq(
      Links.schema.create,
      Links forceInsertAll Seq(
        Link(1, "http://howtographql.com", "Awesome community driven GraphQL tutorial"),
        Link(2, "http://graphql.org", "Official GraphQL web page"),
        Link(3, "https://facebook.github.io/graphql/", "GraphQL specification")
      )
    )


}
