package mywork1

import mods.Employee
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Random

object FetchAndSaveEmployeeService {

  val logger: Logger = LoggerFactory.getLogger(FetchAndSaveEmployeeService.getClass.getName)

  def main(args: Array[String]): Unit = {
    saveEmployeeInfinite()
  }

  private def getRandomEmployeeDetails: Employee = {
    Employee(Random.nextInt(10000),
      listOfNames(Random.nextInt(listOfNames.length)).toUpperCase,
      designationList(Random.nextInt(designationList.length)),
      123.12, Random.nextBoolean())
  }

  val designationList = List(
    "CEO", "Software Engineer", "Sr. Software Engineer", "Trainee", "Intern", "Business Analyst"
  )

  val listOfNames = List(
    "foo", "bar", "baz", "qux",
    "quux", "corge", "grault",
    "garply", "waldo", "fred",
    "plugh", "xyzzy", "thud"
  )

  val employeeDb: DAO = DBSchema.createDatabase

  private def saveEmployeeInfinite(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    var end: Int = 100
    while (end >= 1) {
      employeeDb.addEmployee(getRandomEmployeeDetails).map(println)
      println("Employee Added")
      Thread.sleep(1000)
      end -= 1
    }
  }
}
