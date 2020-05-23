import howto.DAO

package object models {

  case class Link(id: Int, url: String, description: String)
  case class MyContext(dao: DAO)
}
