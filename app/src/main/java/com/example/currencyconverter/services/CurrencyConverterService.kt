package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.models.Currency

class CurrencyConverterService(
    context: Context
) {
    private val currencyManagerDBService = CurrencyManagerDBService(context)
    private val currencies: List<Currency> = this.currencyManagerDBService.getAllCurrencies()


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

        return amount * (toRate / fromRate)
    }

    fun getCurrencyByCode(code: String): Currency? {
        return this.currencies.find { it.code == code }
    }
}
