package com.app.realprice.services

import android.content.Context
import com.app.realprice.dao.CurrencyInfoDAO
import com.app.realprice.dao.ExchangeRateDAO
import com.app.realprice.dao.UpdateDateDAO
import com.app.realprice.models.Currency

class CurrencyManagerDBService(
    context: Context
) {
    private val currencyInfoDAO = CurrencyInfoDAO(context)
    private val exchangeRateDAO = ExchangeRateDAO(context)
    private val apiService = ApiService.getInstance(context)
    private val updateDateDAO = UpdateDateDAO(context)

    val currencies: List<Currency> = this.getAllCurrencies()
    private val popularCurrency: List<Currency>

    init {
        val popularCodes = listOf("USD", "EUR", "JPY", "GBP", "CNY")
        this.popularCurrency = this.currencies.filter { it.code in popularCodes }
    }

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
                        code.value,
                        flag = if (currency["code"] != null) "_" + currency["code"]?.lowercase() else ""
                    )
                )
            }
        }

        return currencies.sortedBy { it.code }
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
        return this.popularCurrency
    }

    fun getCurrencyByCode(code: String): Currency? {
        return this.currencies.find { it.code == code }
    }

    fun haveNoData(): Boolean {
        return this.currencies.isEmpty()
    }
}
