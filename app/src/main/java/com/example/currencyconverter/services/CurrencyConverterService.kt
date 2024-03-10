package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.models.Currency

class CurrencyConverterService private constructor(
    context: Context
) {
    private val currencies: List<Currency>

    init {
        val currencyManagerDBService = CurrencyManagerDBService(context)
        this.currencies = currencyManagerDBService.getAllCurrencies()
    }

    /**
     * Convert the amount from one currency to another
     * @param fromCurrency - The currency to convert from
     * @param toCurrency - The currency to convert to
     * @param amount - The amount to convert
     * @return Double - The converted amount
     */
    fun convert(fromCurrency: String, toCurrency: String, amount: Double): Double {
        val fromCurrencyInfo = this.currencies.find { it.code == fromCurrency }
        val toCurrencyInfo = this.currencies.find { it.code == toCurrency }

        if (fromCurrencyInfo == null || toCurrencyInfo == null) {
            throw IllegalArgumentException("Invalid currency code")
        }

        val fromRate = fromCurrencyInfo.rate
        val toRate = toCurrencyInfo.rate

        return amount * (toRate / fromRate)
    }

    companion object {
        @Volatile
        private var INSTANCE: CurrencyConverterService? = null

        /**
         * Get the instance of the CurrencyConverterService
         * @return CurrencyConverterService - The instance of the CurrencyConverterService
         */
        fun getInstance(context: Context): CurrencyConverterService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CurrencyConverterService(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}
