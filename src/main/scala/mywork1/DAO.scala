package mywork1

import mods.Employee
import scala.concurrent.Future

class DAO {
  def allEmployees: Future[Seq[Employee]] = DBSchema.getAllEmployees

  def addEmployee(employee: Employee): Future[Employee] = DBSchema.addEmployee(employee)

  def getFirstEmployeeAdded: Future[Employee] = DBSchema.getFirstEmployee
}
