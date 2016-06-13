package lettershop

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import upickle.default._

class PostLettersTest(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  "PostLettersIntoCart" should {

    val cart = "cart/444"
    val post = for {
      _ <- go(HttpRequest(method = PUT, uri = url(cart))) //clean the cart
      abc <- go(HttpRequest(method = POST, uri = url(s"$cart/abc")))
    } yield abc
    val getCart = for {
      _ <- post
      _ <- go(HttpRequest(method = POST, uri = url(s"$cart/XY")))
      get <- go(HttpRequest(uri = url(cart)))
    } yield get

    "respond with status 200" >> {
      post.map(_.status) should ===(ok200).awaitFor(timeout)
    }

    "add letters to cart" >> {
      body(getCart).map(read[Cart]) should ===(Cart("abcXY")).awaitFor(timeout)
    }

  }

}
