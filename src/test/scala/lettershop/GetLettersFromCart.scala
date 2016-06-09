package lettershop

import org.specs2.mutable.Specification
import org.specs2.concurrent.ExecutionEnv

import akka.http.scaladsl.model._
import upickle.default._

case class Cart(letters: String)

class GetLettersIntoCart(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  "GetLettersFromCart" should {

    val getCart = go(HttpRequest(uri = url("cart")))

    "get letters from cart" >> {
      getCart.map(_.status) should === (ok200).awaitFor(timeout)
      body(getCart).map(read[Cart]) should === (Cart("abc")).awaitFor(timeout)
    }

  }

}
