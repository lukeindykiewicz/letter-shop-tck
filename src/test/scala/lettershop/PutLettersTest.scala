package lettershop

import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.HttpRequest
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import upickle.default._

class PutLettersTest(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  "PutLettersIntoCart" should {

    val cart = "cart/a1b2"
    val overwritten = "qwerty"
    val put = go(HttpRequest(method = HttpMethods.PUT, uri = url(s"$cart/abc")))
    val getCart = for {
      _ <- put
      _ <- go(HttpRequest(method = HttpMethods.PUT, uri = url(s"$cart/$overwritten")))
      get <- go(HttpRequest(uri = url(cart)))
    } yield get

    "respond with status 200" >> {
      put.map(_.status) should === (ok200).awaitFor(timeout)
    }

    "overwrite letters in cart" >> {
      body(getCart).map(read[Cart]) should === (Cart(overwritten)).awaitFor(timeout)
    }

  }

}
