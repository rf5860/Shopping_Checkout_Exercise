package com.rjf.dius.shopping.model

import java.math.BigDecimal

/**
 * Different pricing rules that can apply during checkout.
 */
sealed class PricingRule {
    /**
     * A pricing rule for "Buy <X> for the price of <Y>" type discounts.
     * @property item the item the rule applies to.
     * @property payFor the amount of items to pay for.
     * @property receive the number of items to receive for free.
     */
    data class PayForXReceiveY(val item: Item, val payFor: Int, val receive: Int) : PricingRule() {
    }

    /**
     * A pricing rule for when a free item is bundled with some other item.
     * @property purchased the item being purchased.
     * @property min the minimum amount to purchase before receiving the free item.
     * @property bundled the item included for free.
     */
    data class BundleDeal(val purchased: Item, val min: Int = 1, val bundled: Item) : PricingRule()

    /**
     * A pricing rule for bulk discounts.
     * @property item the item the rule applies to.
     * @property min the minimum amount to purchase to receive the discount.
     * @property discount the discount to apply.
     */
    data class BulkDiscount(val item: Item, val min: Int, val discount: Discount) : PricingRule() {
        /**
         * @return the discounted price.
         */
        fun pricePerUnit() = when (discount) {
            is Discount.PercentDiscount -> item.price * (BigDecimal.ONE - discount.percentage)
            is Discount.FlatDiscount -> item.price - discount.amount
        }
    }
}