package lettershop

import org.specs2.mutable.Specification
import org.specs2.concurrent.ExecutionEnv

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import scala.concurrent.Future
import upickle.default._

case class Cart(letters: String)

class GetLettersIntoCart(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  val cartF: Future[HttpResponse] = Http().singleRequest(HttpRequest(method = HttpMethods.GET, uri = s"$url/cart"))

  "GetLettersFromCart" should {

    "get letters from cart" >> {
      cartF.map(_.status) should === (ok200).awaitFor(timeout)
      cartF.flatMap(_.entity.toStrict(timeout).map(_.data.utf8String)).map(data => read[Cart](data)) should === (Cart("abc")).awaitFor(timeout)
    }

  }

}
