package com.example.currencyconverter.services

import com.example.currencyconverter.models.Currency

class CurrencyConverterService {
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
}
