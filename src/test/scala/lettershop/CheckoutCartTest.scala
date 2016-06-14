package lettershop

import akka.http.scaladsl.model.HttpRequest
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification

import upickle.default._

class CheckoutCartTest(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest {

  "ChackoutCartTest" should {

    "return status 200" >> {
      val resp = checkoutCart("asdf")
      resp.map(_.status) should ===(ok200).awaitFor(timeout)
    }

    "return price and receipt id" >> {
      val resp = checkoutCart("zxcv")
      val pr = body(resp).map(read[PriceAndReceipt])
      pr.map(_.price) should ===(40.0).awaitFor(timeout)
      pr.map(_.receiptId) should not be empty.awaitFor(timeout)
    }

    "empty cart after checkout" >> {
      val cartId = "abc123"
      val get = for {
        _ <- checkoutCart("zxcv", cartId = cartId)
        g <- go(HttpRequest(uri = url(s"cart/$cartId")))
      } yield g
      body(get).map(read[Cart](_).letters) should ===("").awaitFor(timeout)
    }

  }

}
