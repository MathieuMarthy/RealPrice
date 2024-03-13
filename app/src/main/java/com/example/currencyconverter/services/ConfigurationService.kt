package com.example.currencyconverter.services

import android.content.Context
import com.example.currencyconverter.dao.ConfigurationDAO
import com.example.currencyconverter.models.Configuration

class ConfigurationService(
    private val context: Context
) {
    private lateinit var configurationDao: ConfigurationDAO
    lateinit var configuration: Configuration
        private set

    init {
        this.refresh()
    }

    fun saveCurrency1(currency: String) {
        this.configuration.defaultCurrency1 = currency
        this.configurationDao.save(this.configuration)
    }

    fun saveCurrency2(currency: String) {
        this.configuration.defaultCurrency2 = currency
        this.configurationDao.save(this.configuration)
    }

    fun setAllowMobileData(allow: Boolean) {
        this.configuration.allowMobileData = allow
        this.configurationDao.save(this.configuration)
    }

    fun setBankCharge(active: Boolean) {
        this.configuration.activeBankCharge = active
        this.configurationDao.save(this.configuration)
    }

    fun setTaxRate(rate: Double) {
        this.configuration.taxRate = rate
        this.configurationDao.save(this.configuration)
    }

    fun setFixedTax(fixedTax: Double) {
        this.configuration.fixedTax = fixedTax
        this.configurationDao.save(this.configuration)
    }

    fun refresh() {
        this.configurationDao = ConfigurationDAO(this.context)
        val storedConfig = this.configurationDao.load(Configuration::class.java)

        if (storedConfig == null) {
            this.configuration = Configuration()
            this.configurationDao.save(this.configuration)
        } else {
            this.configuration = storedConfig
        }
    }
}
