package com.rjf.dius.shopping

import com.rjf.dius.shopping.model.Discount
import com.rjf.dius.shopping.model.Item
import com.rjf.dius.shopping.model.Item.*
import com.rjf.dius.shopping.model.PricingRule
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.maps.shouldContainAll
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
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

class CountTests : StringSpec() {
    init {
        "checkout.itemCount" {
            forall(
                row(arrayOf(), 0, 0, 0, 0),
                row(arrayOf(`Super iPad`), 1, 0, 0, 0),
                row(arrayOf(`Macbook Pro`), 0, 1, 0, 0),
                row(arrayOf(`Apple TV`), 0, 0, 1, 0),
                row(arrayOf(`VGA Adapter`), 0, 0, 0, 1),
                row(Array(2) { `Super iPad` } + Array(3) { `Macbook Pro` } + Array(4) { `Apple TV` } + Array(5) { `VGA Adapter` }, 2, 3, 4, 5)
            ) { items, expectedIpads, expectedMacbooks, expectedTvs, expectedAdapters ->
                standardCheckoutWith(*items).apply {
                    itemCount(`Super iPad`) shouldEqual expectedIpads
                    itemCount(`Macbook Pro`) shouldEqual expectedMacbooks
                    itemCount(`Apple TV`) shouldEqual expectedTvs
                    itemCount(`VGA Adapter`) shouldEqual expectedAdapters
                }
            }
        }
    }
}

class ItemCountsTests : StringSpec() {
    init {
        "checkout.itemCounts" {
            forall(
                row(arrayOf(), 0, 0, 0, 0),
                row(arrayOf(`Super iPad`), 1, 0, 0, 0),
                row(arrayOf(`Macbook Pro`), 0, 1, 0, 0),
                row(arrayOf(`Apple TV`), 0, 0, 1, 0),
                row(arrayOf(`VGA Adapter`), 0, 0, 0, 1),
                row(Array(2) { `Super iPad` } + Array(3) { `Macbook Pro` } + Array(4) { `Apple TV` } + Array(5) { `VGA Adapter` }, 2, 3, 4, 5)
            ) { items, expectedIpads, expectedMacbooks, expectedTvs, expectedAdapters ->
                standardCheckoutWith(*items).itemCounts() shouldContainExactly mapOf(
                    `Super iPad` to expectedIpads,
                    `Macbook Pro` to expectedMacbooks,
                    `Apple TV` to expectedTvs,
                    `VGA Adapter` to expectedAdapters
                ).filterValues { it > 0 }
            }
        }
    }
}

class BundledItemsTests : StringSpec() {
    init {
        "checkout.bundledItems" {
            forall(
                row(Array(1) { `VGA Adapter` }, emptyList()),
                row(Array(1) { `Macbook Pro` } + `VGA Adapter`, List(1) { `VGA Adapter` }),
                row(Array(2) { `Macbook Pro` } + `VGA Adapter`, List(2) { `VGA Adapter` }),
                row(Array(2) { `Macbook Pro` } + Array(2) { `VGA Adapter` }, List(2) { `VGA Adapter` })
            ) { items, expectedBundledItems ->
                standardCheckoutWith(*items).bundledItems() shouldEqual expectedBundledItems
            }
        }
    }
}

class IncludedItemsTests : StringSpec() {
    init {
        "checkout.includedItems" {
            forall(
                row(Array(1) { `Apple TV` }, emptyList()),
                row(Array(2) { `Apple TV` }, emptyList()),
                row(Array(3) { `Apple TV` }, List(1) { `Apple TV` }),
                row(Array(4) { `Apple TV` }, List(1) { `Apple TV` }),
                row(Array(5) { `Apple TV` }, List(1) { `Apple TV` }),
                row(Array(6) { `Apple TV` }, List(2) { `Apple TV` })
            ) { items, expectedIncludedItems ->
                standardCheckoutWith(*items).includedItems() shouldEqual expectedIncludedItems
            }
        }
    }
}

class FreeItemsTests : StringSpec() {
    init {
        "checkout.freeItems" {
            forall(
                row(Array(2) { `Macbook Pro` } + Array(2) { `Apple TV` }, List(2) { `VGA Adapter` }),
                row(Array(6) { `Apple TV` }, List(2) { `Apple TV` }),
                row(Array(3) { `Macbook Pro` } + Array(2) { `VGA Adapter` }, List(3) { `VGA Adapter` }),
                row(Array(3) { `Macbook Pro` } + Array(2) { `VGA Adapter` } + Array(6) { `Apple TV` }, List(3) { `VGA Adapter` } + List(2) { `Apple TV` })
            ) { items, expectedFreeItems ->
                standardCheckoutWith(*items).freeItems() shouldContainExactlyInAnyOrder expectedFreeItems
            }
        }
    }
}