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

  def checkCartPrice(letters: String, promoCode: Option[String] = None) = {
    val cartId = UUID.randomUUID.toString
    val promo = promoCode.map(p => s"?promo=$p").getOrElse("")
    for {
      _ <- go(HttpRequest(PUT, url(s"cart/$cartId/$letters")))
      check <- go(HttpRequest(uri = url(s"check/$cartId$promo")))
    } yield check
  }

  "CheckCartPriceTest" should {

    "respond with status 200" >> {
      val price = checkCartPrice("abcd")
      price.map(_.status) should ===(ok200).awaitFor(timeout)
    }

    "return price as a sum of letters when no promotions are available" >> {
      val price = checkCartPrice("abcd")
      body(price).map(read[Price]) should ===(Price(40)).awaitFor(timeout)
    }

    "use Three For Two for 'a' promotion when 3 'a' letters" >> {
      val price = checkCartPrice("abacda")
      body(price).map(read[Price]) should ===(Price(50)).awaitFor(timeout)
    }

    "use Three For Two for 'a' promotion when 7 'a' letters" >> {
      val price = checkCartPrice("abacadaeafaga")
      body(price).map(read[Price]) should ===(Price(110)).awaitFor(timeout)
    }

    "use Three For Two for 'a' and 'X' promotion when 4 'a' and 3 'X' letters" >> {
      val price = checkCartPrice("aaaaXXX")
      body(price).map(read[Price]) should ===(Price(50)).awaitFor(timeout)
    }

    "use Three For Two for 'a' and 'X' promotion when 2 'a' and 1 'X' letter" >> {
      val price = checkCartPrice("aaX")
      body(price).map(read[Price]) should ===(Price(30)).awaitFor(timeout)
    }

  }

  "CheckCartPriceTest with promo code" should {

    "use '10percent' promo code for 10 percent sale" >> {
      val price = checkCartPrice("qwerty", Some("10percent"))
      body(price).map(read[Price]) should ===(Price(54)).awaitFor(timeout)
    }

    "other than '10percent' return regular price" >> {
      val price = checkCartPrice("qwerty", Some("20percent"))
      body(price).map(read[Price]) should ===(Price(60)).awaitFor(timeout)
    }

    "use all available promotions" >> {
      val price = checkCartPrice("qaweartay", Some("10percent"))
      body(price).map(read[Price]) should ===(Price(72)).awaitFor(timeout)
    }

  }

}
