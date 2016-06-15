package lettershop

case class Cart(letters: String)
case class Price(price: Double)
case class PriceAndReceipt(price: Double, receiptId: String)
case class ReceiptHistory(price: Double, receiptId: String, letters: String)

