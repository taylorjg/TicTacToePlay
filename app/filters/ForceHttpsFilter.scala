package filters

import javax.inject._

import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

// http://stackoverflow.com/questions/19147147/how-to-force-play-framework-2-to-always-use-ssl

@Singleton
class ForceHttpsFilter @Inject()(implicit override val mat: Materializer,
                                 exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    requestHeader.headers.get("x-forwarded-proto") match {
      case Some(header) => {
        if (header == "https") {
          nextFilter(requestHeader) map { result =>
            result.withHeaders("Strict-Transport-Security" -> "max-age=31536000")
          }
        }
        else {
          val secureUrl = s"https://${requestHeader.host}${requestHeader.uri}"
          Future.successful(Results.MovedPermanently(secureUrl))
        }
      }
      case _ => nextFilter(requestHeader)
    }
  }
}
