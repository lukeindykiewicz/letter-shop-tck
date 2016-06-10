package lettershop

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpEntity
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import upickle.default._

case class Price(price: Double)

class PutLetterPriceTest(implicit ee: ExecutionEnv)
  extends Specification
  with BaseTckTest {

  "PutLetterPrice" should {

    val letter = "A"
    val price: Double = 13.5
    val cartId = "888"
    val cart = s"cart/cartId"

    val priceLetter = go(HttpRequest(
      method = PUT,
      uri = url(s"price/$letter"),
      entity = HttpEntity(ContentTypes.`application/json`, write(Price(price)))))

    val checkPrice = for {
      _ <- priceLetter
      _ <- go(HttpRequest(method = PUT, uri = url(s"$cart/$letter")))
      check <- go(HttpRequest(uri = url(s"check/$cartId")))
    } yield check

    "respond with status 200" >> {
      priceLetter.map(_.status) should === (ok200).awaitFor(timeout)
    }

    "letter should have set cost" >> {
      body(checkPrice).map(read[Price]) should === (Price(price)).awaitFor(timeout) 
    }

  }

}
