import mywork1.DAO

package object mods {

  case class Employee(id: Int, name: String, designation: String, salary: Double, active: Boolean)

  case class MyWorkContext(dao: DAO)

}
