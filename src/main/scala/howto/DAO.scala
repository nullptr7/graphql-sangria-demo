package howto

import slick.jdbc.H2Profile.api.{streamableQueryActionExtensionMethods, Database}
import DBSchema._
import models.Link
import scala.concurrent.Future

class DAO(db: Database) {
  def allLinks: Future[Seq[Link]] = db.run(Links.result)
}
