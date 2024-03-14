package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.models.Currency

class CurrencyConverterService(
    context: Context
) {
    private val configurationService = ConfigurationService(context)

    /**
     * Convert the amount from one currency to another
     * @param fromCurrency - The currency to convert from
     * @param toCurrency - The currency to convert to
     * @param amount - The amount to convert
     * @return Double - The converted amount
     */
    fun convert(fromCurrency: Currency, toCurrency: Currency, amount: Double): Double {
        val fromRate = fromCurrency.rate
        val toRate = toCurrency.rate

        if (amount == 0.0) return 0.0

        return this.calculAmount(amount, toCurrency) * (toRate / fromRate)
    }

    private fun calculAmount(amount: Double, toCurrency: Currency): Double {
        if (
            this.configurationService.configuration.activeBankCharge &&
            toCurrency.code == this.configurationService.configuration.taxCurrency
        ) {
            val taxRate = this.configurationService.configuration.taxRate / 100
            val fixedTax = this.configurationService.configuration.fixedTax
            val taxes = (fixedTax + amount * taxRate)

            return amount + taxes
        }
        return amount
    }

    fun refreshConfig() {
        this.configurationService.refresh()
    }
}
