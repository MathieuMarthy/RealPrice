package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.dao.CurrencyInfoDAO
import com.example.currencyconverter.dao.ExchangeRateDAO
import com.example.currencyconverter.models.Currency

class CurrencyManagerDBService(
    context: Context
) {
    private val currencyInfoDAO = CurrencyInfoDAO(context)
    private val exchangeRateDAO = ExchangeRateDAO(context)

    fun getAllCurrencies(): List<Currency> {
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
}
