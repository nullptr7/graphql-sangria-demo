package model

object Testing extends App {

  class ParentModel(val someValue: String) {

    override def toString = s"ParentModel($someValue)"
  }

  case class SampleModel(fields: List[ParentModel]) extends ParentModel("456") {

    def getMe: SampleModel = this

    def getSampleModel: List[ParentModel] = fields
  }

  object SampleModel {
    val empty: SampleModel = SampleModel(List.empty[ParentModel])

    def apply(fields: List[ParentModel]): SampleModel = new SampleModel(fields)
  }

  val SampleModel(fields) = SampleModel(
    List(
      new ParentModel("123"),
      new ParentModel("999"),
      new ParentModel("000"),
    ))

  fields.foreach(println)

}

