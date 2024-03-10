package com.example.currencyconverter.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.currencyconverter.R
import com.example.currencyconverter.models.Currency
import com.example.currencyconverter.services.CurrencyConverterService
import com.example.currencyconverter.services.CurrencyManagerDBService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private lateinit var currencyConverterService: CurrencyConverterService
    private lateinit var currencyManagerDBService: CurrencyManagerDBService

    private val currencyCode1: String = "EUR"
    private val currencyCode2: String = "JPY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // init service
        this.currencyManagerDBService = CurrencyManagerDBService(this)

        this.refreshLastUpdateDate()

        // update the currency exchange rate if it's not already updated today
        if (!this.alreadyUpdateToday() && this.isNetworkAvailable()) {
            this.currencyManagerDBService.updateExchangeRate {
                this.initConverterAndRefreshDate()
            }
        } else {
            this.initConverterAndRefreshDate()
        }

        val currency = Currency("EUR", "Euro", "€", 2.3)
        this.setFirstCurrency(currency)
        this.setSecondCurrency(currency)

    }

    private fun setFirstCurrency(currency: Currency) {
        this.setSymbolOnInput(
            findViewById(R.id.currency_input_1),
            currency
        )
    }

    private fun setSecondCurrency(currency: Currency) {
        this.setSymbolOnInput(
            findViewById(R.id.currency_input_2),
            currency
        )
    }

    private fun setSymbolOnInput(input: ConstraintLayout, currency: Currency) {
        val button = input.findViewById<Button>(R.id.currency_input_dropDown_curreny)

        button.text = currency.symbol
    }


    /**
     * Initialize the currency converter service and refresh the last update date on the UI
     */
    private fun initConverterAndRefreshDate() {
        this.currencyConverterService = CurrencyConverterService(this)
        this.refreshLastUpdateDate()
    }

    /**
     * Refresh the last update date on the UI
     */
    private fun refreshLastUpdateDate() {
        val date = this.currencyManagerDBService.getLastUpdateDate()
        val textView = findViewById<TextView>(R.id.testest)

        val now = LocalDate.now()
        if (date == null || date.toLocalDate() == now) { // if the last update is today
            // hide the last update date
            textView.visibility = View.GONE
        } else {
            // show the last update date
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            textView.text = this.getString(R.string.last_update_at, date.format(formatter))
        }
    }

    /**
     * Check if the network is available
     * @return Boolean - True if the network is available, False otherwise
     */
    private fun isNetworkAvailable(): Boolean {
        return true
    }

    /**
     * Check if the currency exchange rate has already been updated today
     * @return Boolean - True if the currency exchange rate has already been updated today, False otherwise
     */
    private fun alreadyUpdateToday(): Boolean {
        val now = LocalDateTime.now()
        val lastUpdateDate = this.currencyManagerDBService.getLastUpdateDate() ?: return false

        // .toLocalDate() to compare only the day not the hour and minutes
        return now.toLocalDate() == lastUpdateDate.toLocalDate()
    }
}
