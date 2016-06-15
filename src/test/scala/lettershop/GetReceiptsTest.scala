package lettershop

import akka.http.scaladsl.model.HttpRequest
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.Specification

import upickle.default._

import scala.language.experimental.macros

class GetReceiptsTest(implicit ee: ExecutionEnv)
    extends Specification
    with BaseTckTest
    with MatcherMacros {

  "GetReceiptsTest" should {

    def get = go(HttpRequest(uri = url("receipt")))

    "respond with status 200" >> {
      get.map(_.status) should ===(ok200).awaitFor(timeout)
    }

    "has price, receiptId and letters in response" >> {
      val abc = "ABC"
      val de = "DE"
      val fghi = "FGHI"

      val resp = for {
        _ <- checkoutCart(abc)
        _ <- checkoutCart(de)
        _ <- checkoutCart(fghi)
        hist <- go(HttpRequest(uri = url("receipt")))
      } yield hist

      val receipts = body(resp).map(read[List[ReceiptHistory]])
      receipts.map(_.length) should be_>=(3).awaitFor(timeout)
      receipts should contain(allOf(
        matchA[ReceiptHistory].letters(abc).price(30),
        matchA[ReceiptHistory].letters(de).price(20),
        matchA[ReceiptHistory].letters(fghi).price(40)
      )).awaitFor(timeout)
    }

  }

}
