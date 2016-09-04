package filters

import akka.stream.Materializer
import javax.inject._

import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

// http://stackoverflow.com/questions/19147147/how-to-force-play-framework-2-to-always-use-ssl

@Singleton
class ForceHttpsFilter @Inject()(implicit override val mat: Materializer,
                                 exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    Logger.info("Checking for x-forwarded-proto header")
    requestHeader.headers.get("x-forwarded-proto") match {
      case Some(header) if (header != "https") => {
        val secureUrl = s"https://${requestHeader.host}${requestHeader.uri}"
        Logger.info(s"secureUrl: $secureUrl")
        Future.successful(Results.MovedPermanently(secureUrl))
      }
      case _ => nextFilter(requestHeader)
    }
  }
}
