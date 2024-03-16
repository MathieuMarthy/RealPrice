package com.example.realprice.services

import android.content.Context
import com.example.realprice.models.Currency

class CurrencyConverterService(
    context: Context
) {
    private val configurationService = ConfigurationService(context)

    /**
     * Convert the amount from one currency to another
     * @param fromCurrency - The currency to convert from
     * @param toCurrency - The currency to convert to
     * @param amount - The amount to convert
     * @return The converted amount and the amount of taxes
     */
    fun convert(
        fromCurrency: Currency,
        toCurrency: Currency,
        amount: Double
    ): Pair<Double, Double> {
        val fromRate = fromCurrency.rate
        val toRate = toCurrency.rate

        if (amount == 0.0) return Pair(0.0, 0.0)

        val realAmount = amount * (toRate / fromRate)
        val taxesAmount = this.calculateTaxes(realAmount, toCurrency)

        return Pair(realAmount, taxesAmount)
    }

    private fun calculateTaxes(amount: Double, toCurrency: Currency): Double {
        if (
            this.configurationService.configuration.activeBankCharge &&
            toCurrency.code == this.configurationService.configuration.taxCurrency
        ) {
            val taxRate = this.configurationService.configuration.taxRate / 100
            val fixedTax = this.configurationService.configuration.fixedTax
            var taxes = (fixedTax + amount * taxRate)

            if (
                this.configurationService.configuration.limitTax != 0.0 &&
                taxes > this.configurationService.configuration.limitTax
            ) {
                taxes = this.configurationService.configuration.limitTax
            }

            return taxes
        }

        return 0.0
    }

    fun refreshConfig() {
        this.configurationService.refresh()
    }
}
