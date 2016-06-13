package lettershop

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpMethods._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import java.util.UUID

import upickle.default._

class CheckCartPriceTest(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  def checkCartPrice(letters: String) = {
    val cartId = UUID.randomUUID.toString
    for {
      _ <- go(HttpRequest(PUT, url(s"cart/$cartId/$letters")))
      check <- go(HttpRequest(uri = url(s"check/$cartId")))
    } yield check
  }

  "CheckCartPriceTest" should {

    "respond with status 200" >> {
      val price = checkCartPrice("abcd")
      price.map(_.status) should ===(ok200).awaitFor(timeout)
    }

    "Return price as a sum of letters when no promotions are available" >> {
      val price = checkCartPrice("abcd")
      body(price).map(read[Price]) should ===(Price(40)).awaitFor(timeout)
    }

    "Three for two for 'a' promotion with 3 'a' letters" >> {
      val price = checkCartPrice("abacda")
      body(price).map(read[Price]) should ===(Price(50)).awaitFor(timeout)
    }

    "Three for two for 'a' promotion with 7 'a' letters" >> {
      val price = checkCartPrice("abacadaeafaga")
      body(price).map(read[Price]) should ===(Price(110)).awaitFor(timeout)
    }

    "Three for two for 'a' and 'X' promotion with 4 'a' and 3 'X' letters" >> {
      val price = checkCartPrice("aaaaXXX")
      body(price).map(read[Price]) should ===(Price(50)).awaitFor(timeout)
    }

    "Three for two for 'a' and 'X' promotion with 2 'a' and 1 'X' letter" >> {
      val price = checkCartPrice("aaX")
      body(price).map(read[Price]) should ===(Price(30)).awaitFor(timeout)
    }

  }

}
