package com.example.currencyconverter.models

class Configuration(
    var defaultCurrency1: String = "EUR",
    var defaultCurrency2: String = "JPY",
    var allowMobileData: Boolean = false,
    var activeBankCharge: Boolean = false,
    var taxRate: Double = 0.0,
    var fixedTax: Double = 0.0,
    var taxCurrency: String = "EUR",
    var limitTax: Double = 0.0
)
