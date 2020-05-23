package mywork.models

import scala.concurrent.Future

case class Employee(id: Int, name: String, designation: String, salary: Double, active: Boolean)

trait EmployeeRepo {
  def addEmployee(emp: Employee): Future[Employee]

  def getEmployees: Future[Seq[Employee]]
}