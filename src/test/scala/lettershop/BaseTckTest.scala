package lettershop

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import scala.language.postfixOps

trait BaseTckTest{

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val ec = ExecutionContext.Implicits.global

  lazy val conf = ConfigFactory.load();

  private lazy val host = conf.getString("host")
  private lazy val port = conf.getString("port")
  lazy val url = s"http://$host:$port"

  val ok200: StatusCode = StatusCodes.OK

  val timeout = 2 seconds

}
