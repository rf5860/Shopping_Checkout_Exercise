package com.rjf.dius.shopping

import com.rjf.dius.shopping.model.Discount
import com.rjf.dius.shopping.model.Item
import com.rjf.dius.shopping.model.Item.*
import com.rjf.dius.shopping.model.PricingRule
import io.kotlintest.specs.WordSpec
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import io.kotlintest.shouldBe as shouldEqual

private fun standardCheckoutWith(vararg items: Item) = Checkout(
    mutableListOf(
        PricingRule.BundleDeal(`Macbook Pro`, 1, `VGA Adapter`),
        PricingRule.PayForXReceiveY(`Apple TV`, 3, 1),
        PricingRule.BulkDiscount(`Super iPad`, 4, Discount.FlatDiscount(BigDecimal(50)))
    )
).apply { scan(*items) }

private infix fun BigDecimal.shouldBe(expected: BigDecimal) = this.setScale(2, HALF_UP) shouldEqual expected.setScale(2, HALF_UP)
private infix fun BigDecimal.shouldBe(expected: Double) = this shouldBe BigDecimal(expected)
class TotalTests : WordSpec() {

    init {
        "checkout.total" should {
            "respect 2for1 discount" {
                standardCheckoutWith(`Apple TV`, `Apple TV`, `Apple TV`, `VGA Adapter`).total() shouldBe 249.00
            }
            "respect bulk discounts and handle unused 2for1 discount" {
                standardCheckoutWith(`Apple TV`, `Super iPad`, `Super iPad`, `Apple TV`, `Super iPad`, `Super iPad`, `Super iPad`).total() shouldBe 2718.95
            }
            "respect bundled item discount" {
                standardCheckoutWith(`Macbook Pro`, `VGA Adapter`, `Super iPad`).total() shouldBe 1949.98
            }
            "handle unused 2for1 discount" {
                standardCheckoutWith(`Apple TV`, `Apple TV`, `VGA Adapter`).total() shouldBe 249.00
            }
            "handle unused bundled item discount" {
                standardCheckoutWith(`Macbook Pro`, `Super iPad`).total() shouldBe 1949.98
            }
        }
    }
}