package mywork.service

import java.util.UUID

import finalServer.Server.system
import mywork.db.{DBService, DataLayer}
import mywork.models.Employee

import scala.util.Random

object FetchAndPersistService {

  def main(args: Array[String]): Unit = {
    //saveEmployeeInfinite()
  }

  private def getRandomEmployeeDetails: Employee = {
    Employee(Random.nextInt(10000),
      listOfNames(Random.nextInt(listOfNames.length)).toUpperCase,
      designationList(Random.nextInt(designationList.length)),
      123.12, Random.nextBoolean())
  }

/*  import system.dispatcher

  val employeeDb: DataLayer = DBService.createDatabase

  def saveEmployeeInfinite(): Unit = {
    var end: Int = 100
    while (end >= 1) {
      Thread.sleep(4000)
      new DBService(employeeDb.db).addEmployee(getRandomEmployeeDetails)
      end -= 1
      Thread.sleep(3000)
    }
  }*/

  val designationList = List(
    "CEO", "Software Engineer", "Sr. Software Engineer", "Trainee", "Intern", "Business Analyst"
  )

  val listOfNames = List(
    "foo", "bar", "baz", "qux",
    "quux", "corge", "grault",
    "garply", "waldo", "fred",
    "plugh", "xyzzy", "thud"
  )
}