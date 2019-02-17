package com.rjf.dius.shopping.model

import java.math.BigDecimal

/**
 * An item that can be purchased.
 * @property sku the "stock keeping unit" to uniquely identify the item.
 * @property price the price of the item.
 */
enum class Item(val sku: String, val price: BigDecimal) {
    `Super iPad`("ipd", BigDecimal(549.99)),
    `Macbook Pro`("mbp", BigDecimal(1399.99)),
    `Apple TV`("atv", BigDecimal(109.50)),
    `VGA Adapter`("vga", BigDecimal(30.00))
}