package extensions

import play.api.data.{Field, FieldMapping, Form, FormError}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import views.html.helper.FieldElements

object FormsExtensions {

  import play.api.data.format.Formats.stringFormat
  private val dummyFieldMapping = FieldMapping[String]()
  private val dummyForm = Form(dummyFieldMapping)

  implicit class FormErrorExtensions(formError: FormError) {
    def translatedErrorMessages(implicit messages: Messages): String = {
      val key = formError.key
      val dummyField = Field(dummyForm, key, Seq.empty, None, Seq(formError), None)
      val dummyFieldElement = FieldElements(key, dummyField, HtmlFormat.empty, Map(), messages)
      val keyOrGlobal = if (key.isEmpty) "(global form error)" else key
      dummyFieldElement.errors map (e => s"$keyOrGlobal: $e") mkString ", "
    }
  }

  implicit class FormExtensions[A](form: Form[A]) {
    def translatedErrorMessages(implicit messages: Messages): String = {
      form.errors map (_.translatedErrorMessages) mkString ", "
    }
  }
}
