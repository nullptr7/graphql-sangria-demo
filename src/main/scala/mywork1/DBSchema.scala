package mywork1

import mods.Employee
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.lifted.ProvenShape

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

object DBSchema {

  val db: H2Profile.backend.Database = Database.forConfig("h2mem")
  val dao: DAO = new DAO

  class EmployeeTable(tag: Tag) extends Table[Employee](tag, "EMPLOYEE") {

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey)

    def name: Rep[String] = column[String]("NAME")

    def designation: Rep[String] = column[String]("DESIGNATION")

    def salary: Rep[Double] = column[Double]("SALARY")

    def active: Rep[Boolean] = column[Boolean]("ACTIVE")

    override def * : ProvenShape[Employee] = (id, name, designation, salary, active) <> ((Employee.apply _).tupled, Employee.unapply)
  }

  // Responsible for creating schema and adding three entities via databaseSetup variable
  def createDatabase: DAO = {

    Await.result(db.run(databaseSetup), 10 seconds)
    dao
  }

  def getInstance: DAO = {
    import scala.concurrent.ExecutionContext.Implicits.global
    db.run(Employees.result).map(println)
    Thread.sleep(2000)
    dao
  }

  def addEmployee(employee: Employee): Future[Employee] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    db.run(Employees += employee).map(_ => employee)
  }

  def getAllEmployees: Future[Seq[Employee]] = {
    db.run(Employees.result)
  }

  def getFirstEmployee: Future[Employee] = {
    db.run(Employees.filter(a => a.id > 1).result.head)
  }

  val Employees = TableQuery[EmployeeTable]

  private val databaseSetup =
    DBIO.seq(
      Employees.schema.create
    )
}
