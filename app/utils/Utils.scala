package utils

import play.twirl.api.{Html, HtmlFormat}

object Utils {

  final val MAX_USER_DISPLAY_LENGTH = 15

  def userDisplayName(username: String): Html = {
    if (username.length <= MAX_USER_DISPLAY_LENGTH)
      HtmlFormat.escape(username)
    else {
      val croppedUsername = HtmlFormat escape (username take MAX_USER_DISPLAY_LENGTH)
      val ellipsis = HtmlFormat raw "&hellip;"
      HtmlFormat.fill(List(croppedUsername, ellipsis))
    }
  }
}
