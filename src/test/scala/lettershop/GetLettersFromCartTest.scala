package lettershop

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethods._
import org.specs2.mutable.Specification
import org.specs2.concurrent.ExecutionEnv

import upickle.default._

case class Cart(letters: String)

class GetLettersFromCartTest(implicit ee: ExecutionEnv)
  extends Specification
  with BaseTckTest {
  "GetLettersFromCart" should {

    val cart123 = "cart/123"
    val fooBAR = "fooBAR"
    val  getCart = for {
      _ <- go(HttpRequest(method = PUT, uri = url(s"$cart123/$fooBAR")))
      get <- go(HttpRequest(uri = url(cart123)))
    } yield get

    "respond with status 200" >> {
      getCart.map(_.status) should === (ok200).awaitFor(timeout)
    }

    "contain inserted letters" >> {
      body(getCart).map(read[Cart]) should === (Cart(fooBAR)).awaitFor(timeout)
    }

  }
}
