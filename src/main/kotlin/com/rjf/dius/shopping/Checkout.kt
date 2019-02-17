package com.rjf.dius.shopping

import com.rjf.dius.shopping.model.Item
import com.rjf.dius.shopping.model.PricingRule
import com.rjf.dius.shopping.model.PricingRule.*
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode.DOWN


/**
 * A checkout system for tracking a group of [items].
 *
 * @property pricingRules the pricing rules / discounts to apply to this checkout.
 * @property items the items being checked out.
 * @constructor Creates a new checkout.
 */
class Checkout(private val pricingRules: MutableList<PricingRule>) {
    var items: MutableList<Item> = mutableListOf()

    /**
     * Adds [newItems] to the list of [items] for checkout.
     */
    fun scan(vararg newItems: Item) = items.addAll(newItems)

    /**
     * Calculates the total amount payable.
     * @return the total amount payable.
     */
    fun total(): BigDecimal = items.fold(ZERO) { sum, item -> sum + item.price }.setScale(2, DOWN)
}