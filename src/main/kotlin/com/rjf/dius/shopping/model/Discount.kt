package com.rjf.dius.shopping.model

import java.math.BigDecimal

/**
 * A discount to apply to an [Item].
 */
sealed class Discount {
    /**
     * A percentage discount.
     * @property percentage the percentage to remove from the price.
     */
    data class PercentDiscount(val percentage: BigDecimal) : Discount()

    /**
     * A flat discount.
     * @property amount the amount to remove from the price.
     */
    data class FlatDiscount(val amount: BigDecimal) : Discount()
}
