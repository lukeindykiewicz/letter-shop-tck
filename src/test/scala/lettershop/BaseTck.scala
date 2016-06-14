package lettershop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpMethod
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.concurrent.Future

import scala.language.postfixOps

trait BaseTckTest {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val ec = ExecutionContext.Implicits.global

  lazy val conf = ConfigFactory.load();
  private lazy val host = conf.getString("host")
  private lazy val port = conf.getString("port")

  val ok200: StatusCode = StatusCodes.OK

  val timeout = 2 seconds

  def go(request: HttpRequest): Future[HttpResponse] =
    Http().singleRequest(request)

  def url(path: String): String = s"http://$host:$port/$path"

  def body(respF: Future[HttpResponse]): Future[String] =
    respF.flatMap(_.entity.toStrict(timeout).map(_.data.utf8String))

  private def checkCart(method: HttpMethod, path: String)(letters: String, promoCode: Option[String] = None, cartId: String) = {
    val promo = promoCode.map(p => s"?promo=$p").getOrElse("")
    for {
      _ <- go(HttpRequest(PUT, url(s"cart/$cartId/$letters")))
      check <- go(HttpRequest(method, uri = url(s"$path/$cartId$promo")))
    } yield check
  }

  def checkCartPrice(
    letters: String,
    promoCode: Option[String] = None,
    cartId: String = UUID.randomUUID.toString
  ) =
    checkCart(GET, "check")(letters: String, promoCode: Option[String], cartId)

  def checkoutCart(
    letters: String,
    promoCode: Option[String] = None,
    cartId: String = UUID.randomUUID.toString
  ) =
    checkCart(POST, "checkout")(letters: String, promoCode: Option[String], cartId)

}
