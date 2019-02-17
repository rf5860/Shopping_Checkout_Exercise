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

    private val bundleDeals = pricingRules.filterIsInstance(BundleDeal::class.java)
    private val bulkDiscounts = pricingRules.filterIsInstance(BulkDiscount::class.java)
    private val payForXDeals = pricingRules.filterIsInstance(PayForXReceiveY::class.java)

    /**
     * Get a list of the items which are free because of "Buy <X> for the price of <Y>" type discounts.
     * @return a list of items which are free because of "Buy <X> for the price of <Y>" type discounts.
     */
    fun includedItems() = payForXDeals.flatMap { (item, payFor, receive) -> List(itemCount(item) / payFor * receive) { item } }

    /**
     * Get a list of the items which are free because they're bundled with some other item.
     * @return a list of the items which are free because they're bundled with some other item.
     */
    fun bundledItems() = bundleDeals.flatMap { (purchased, min, bundled) -> List(itemCount(purchased) / min) { bundled } }

    /**
     * Get a list of the items which are included for free (because of pricing rules).
     * @return a list of the items which are included for free (because of pricing rules).
     */
    fun freeItems() = includedItems() + bundledItems()

    /**
     * The number of occurrences of each item.
     * @return the number of occurrences of each item.
     */
    fun itemCounts() = items.groupingBy { it }.eachCount()

    /**
     * The number of occurrences of the given item.
     * @param item the item to count.
     * @return the number of occurrences of the given item.
     */
    fun itemCount(item: Item) = itemCounts()[item] ?: 0

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