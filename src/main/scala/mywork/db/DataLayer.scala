package mywork.db

import mywork.db.DBService.Employees
import mywork.models.Employee
import slick.jdbc.H2Profile.api.{Database, streamableQueryActionExtensionMethods}

import scala.concurrent.Future

class DataLayer(val db: Database) {


  def allEmployees: Future[Seq[Employee]] = db.run(Employees.result)

}
