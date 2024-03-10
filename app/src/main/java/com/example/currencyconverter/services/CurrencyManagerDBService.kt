package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.dao.CurrencyInfoDAO
import com.example.currencyconverter.dao.ExchangeRateDAO
import com.example.currencyconverter.dao.UpdateDateDAO
import com.example.currencyconverter.models.Currency

class CurrencyManagerDBService(
    context: Context
) {
    private val currencyInfoDAO = CurrencyInfoDAO(context)
    private val exchangeRateDAO = ExchangeRateDAO(context)
    private val apiService = ApiService.getInstance(context)
    private val updateDateDAO = UpdateDateDAO(context)

    val currencies: List<Currency> = this.getAllCurrencies()
    private val popularCodes = listOf("USD", "EUR", "JPY", "GBP")

    private fun getAllCurrencies(): List<Currency> {
        val currencies = mutableListOf<Currency>()

        // load currencies informations
        val currenciesInfo = this.currencyInfoDAO.load() ?: emptyList()
        val exchangeRate: Map<String, Double> = this.exchangeRateDAO.load() ?: mapOf()

        // merged the currencies informations with the exchange rate
        for (code in exchangeRate) {
            val currency = currenciesInfo.find { it["code"] == code.key }
            if (currency != null) {
                currencies.add(
                    Currency(
                        code.key,
                        currency["symbol"] ?: "",
                        currency["name"] ?: "",
                        code.value
                    )
                )
            }
        }

        return currencies
    }

    fun updateExchangeRate(callback: () -> Unit) {
        this.apiService.getCurrencyExchangeRate(
            { currencyExchangeRate, lastUpdateDate ->
                // Update the currency exchange rate in the database
                this.updateCurrencies(currencyExchangeRate)
                // Update the last update date in the database
                this.updateDateDAO.save(lastUpdateDate)

                callback()
            },
            {}
        )
    }

    private fun updateCurrencies(currencies: Map<String, Double>) {
        this.exchangeRateDAO.save(currencies)
    }

    fun getLastUpdateDate() = this.updateDateDAO.load()

    fun getPopularCurrencies(): List<Currency> {
        return this.currencies.filter { it.code in this.popularCodes }
    }

    fun getCurrencyByCode(code: String): Currency? {
        return this.currencies.find { it.code == code }
    }
}
