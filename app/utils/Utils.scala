package utils

import play.twirl.api.{Html, HtmlFormat}

object Utils {

  val MAX_USER_DISPLAY_LENGTH = 15

  def userDisplayName(username: String): Html = {
    if (username.length <= MAX_USER_DISPLAY_LENGTH) HtmlFormat.escape(username)
    else {
      val bit1 = HtmlFormat.escape(username.substring(0, MAX_USER_DISPLAY_LENGTH))
      val bit2 = HtmlFormat.raw("&hellip;")
      val bits = scala.collection.immutable.Seq(bit1, bit2)
      HtmlFormat.fill(bits)
    }
  }
}
