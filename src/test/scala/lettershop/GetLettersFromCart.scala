package lettershop

import org.specs2.mutable.Specification
import org.specs2.concurrent.ExecutionEnv

import scala.concurrent.Future

import akka.http.scaladsl.model._
import upickle.default._

case class Cart(letters: String)

class GetLettersFromCart(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  "GetLettersFromCart" should {

    val cart123 = "cart/123"
    val fooBAR = "fooBAR"
    val  getCart: Future[HttpResponse] = for {
      _ <- go(HttpRequest(method = HttpMethods.PUT, uri = url(s"$cart123/$fooBAR")))
      get <- go(HttpRequest(uri = url(cart123)))
    } yield get

    "get letters from cart" >> {
      getCart.map(_.status) should === (ok200).awaitFor(timeout)
      body(getCart).map(read[Cart]) should === (Cart(fooBAR)).awaitFor(timeout)
    }

  }

}
