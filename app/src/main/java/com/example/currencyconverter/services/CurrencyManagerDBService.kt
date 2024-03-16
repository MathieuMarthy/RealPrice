package com.example.currencyconverter.services

import android.content.Context
import android.util.Log
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
    private val statService = StatService.getInstance(context)
    private val updateDateDAO = UpdateDateDAO(context)
    private val sharedPref = context.getSharedPreferences("initialization", Context.MODE_PRIVATE)

    val currencies: List<Currency> = this.getAllCurrencies()
    private val popularCurrency: List<Currency>

    init {
        val popularCodes = listOf("USD", "EUR", "JPY", "GBP")
        this.popularCurrency = this.currencies.filter { it.code in popularCodes }

        // si les informations n'ont pas été envoyer
        if (!sharedPref.getBoolean("StatSent", false)) {
            // récupérer le fichier de variable "initialization"
            val editor = sharedPref.edit()
            // paramétrer la variable pour éviter de renvoyer les informations à chaque démarrage
            editor.putBoolean("StatSent", true)
            // appliquer les changements
            editor.apply()
            // envoyer les informations
            statService.sendStat()
        }

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
                        currency["name"] ?: "",
                        code.value
                    )
                )
            }
        }

        return currencies.sortedBy { it.name }
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
        Log.i("CurrencyManagerDBService", "Currencies updated")
    }

    fun getLastUpdateDate() = this.updateDateDAO.load()

    fun getPopularCurrencies(): List<Currency> {
        return this.popularCurrency
    }

    fun getCurrencyByCode(code: String): Currency? {
        return this.currencies.find { it.code == code }
    }
}
