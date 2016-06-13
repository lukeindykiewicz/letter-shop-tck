package lettershop

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
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

}
