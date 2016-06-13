package lettershop

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpEntity
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import upickle.default._

class PutLetterPriceTest(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  "PutLetterPrice" should {

    val letter = "A"
    val price: Double = 13.5

    val priceLetter = go(HttpRequest(
      method = PUT,
      uri = url(s"price/$letter"),
      entity = HttpEntity(ContentTypes.`application/json`, write(Price(price)))
    ))

    def checkPrice(cartId: String, letter: String) =
      for {
        _ <- go(HttpRequest(method = PUT, uri = url(s"cart/$cartId/$letter")))
        check <- go(HttpRequest(uri = url(s"check/$cartId")))
      } yield check

    val all = for {
      _ <- priceLetter
      check <- checkPrice("888", letter)
    } yield check

    "respond with status 200" >> {
      priceLetter.map(_.status) should ===(ok200).awaitFor(timeout)
    }

    "letter should have set cost" >> {
      body(all).map(read[Price]) should ===(Price(price)).awaitFor(timeout)
    }

    "default letter price is 10" >> {
      body(checkPrice("777", "x")).map(read[Price]) should ===(Price(10)).awaitFor(timeout)
    }

  }

}
