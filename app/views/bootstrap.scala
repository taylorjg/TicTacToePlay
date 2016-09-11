package views.html.helper
import play.twirl.api.Html

package object bootstrap {
  implicit val fieldConstructor = new FieldConstructor {
    override def apply(elements: FieldElements): Html = views.html.bootstrapFieldConstructor(elements)
  }
}
