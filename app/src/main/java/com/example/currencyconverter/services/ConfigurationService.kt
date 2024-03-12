package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.dao.ConfigurationDAO
import com.example.currencyconverter.models.Configuration

class ConfigurationService(
    context: Context
) {
    private val configurationDao = ConfigurationDAO(context)
    var configuration: Configuration
        private set

    init {
        val storedConfig = this.configurationDao.load(Configuration::class.java)

        if (storedConfig == null) {
            this.configuration = Configuration("EUR", "JPY")
            this.configurationDao.save(this.configuration)
        } else {
            this.configuration = storedConfig
        }
    }

    fun saveCurrency1(currency: String) {
        this.configuration.defaultCurrency1 = currency
        this.configurationDao.save(this.configuration)
    }

    fun saveCurrency2(currency: String) {
        this.configuration.defaultCurrency2 = currency
        this.configurationDao.save(this.configuration)
    }
}
