package mywork.db

import mywork.models.Employee
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps

object DBService {

  def createDatabase(implicit ec: ExecutionContext): DataLayer = {
    val db: H2Profile.backend.Database = Database.forConfig("memoryDb")

    val ddl = DBIO.seq(
      (Employees schema).create,
    )

    Await.result(db.run(ddl), 10 seconds)

    new DataLayer(db)
  }

  class EmployeeTable(tag: Tag) extends Table[Employee](tag, "EMPLOYEE") {

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey)

    def name: Rep[String] = column[String]("NAME")

    def designation: Rep[String] = column[String]("DESIGNATION")

    def salary: Rep[Double] = column[Double]("SALARY")

    def active: Rep[Boolean] = column[Boolean]("ACTIVE")

    override def * : ProvenShape[Employee] = (id, name, designation, salary, active) <> ((Employee.apply _).tupled, Employee.unapply)
  }

  var Employees: TableQuery[EmployeeTable] = TableQuery[EmployeeTable]
}
