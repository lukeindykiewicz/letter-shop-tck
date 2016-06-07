package lettershop

import akka.actor.ActorSystem
import org.specs2.mutable.Specification
import org.specs2.concurrent.ExecutionEnv

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

class PutLettersIntoCart(implicit ee: ExecutionEnv) extends Specification {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  import ExecutionContext.Implicits.global

  val host = "http://google.com"

  val cartF: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"$host"))

  "PutLettersIntoCart" should {

    "get letters from cart" >> {
      val ok: StatusCode = StatusCodes.OK
      cartF.map(_.status) should be(ok).awaitFor(2 seconds)
    }
git con
  }

}
