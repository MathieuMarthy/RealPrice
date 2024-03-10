package com.example.currencyconverter.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
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

    private lateinit var currency1: Currency
    private lateinit var currency2: Currency

    private lateinit var input1: ConstraintLayout
    private lateinit var input2: ConstraintLayout

    private var inConversion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // init
        this.currencyManagerDBService = CurrencyManagerDBService(this)

        this.input1 = findViewById(R.id.currency_input_1)
        this.input2 = findViewById(R.id.currency_input_2)

        this.refreshLastUpdateDate()

        // update the currency exchange rate if it's not already updated today
        if (!this.alreadyUpdateToday() && this.isNetworkAvailable()) {
            this.currencyManagerDBService.updateExchangeRate {
                this.initConverterAndRefreshDate()
            }
        } else {
            this.initConverterAndRefreshDate()
        }

        // set the default currency
        this.currency1 = this.currencyConverterService.getCurrencyByCode("EUR")!!
        this.currency2 = this.currencyConverterService.getCurrencyByCode("JPY")!!


        // set the currency on the UI
        this.setSymbolOnInput(this.input1, this.currency1)
        this.setSymbolOnInput(this.input2, this.currency2)

        // set listeners
        val numInput1 = this.input1.findViewById<EditText>(R.id.currency_input_money_amount)
        val numInput2 = this.input2.findViewById<EditText>(R.id.currency_input_money_amount)

        numInput1.addTextChangedListener {
            if (inConversion) {
                this.inConversion = false
                return@addTextChangedListener
            }

            val amount = it.toString()
            if (amount.isBlank()) {
                return@addTextChangedListener
            }

            val convertedAmount = this.currencyConverterService.convert(
                this.currency1, this.currency2, amount.toDouble()
            )
            this.inConversion = true
            this.setAmount(this.input2, convertedAmount)
        }

        numInput2.addTextChangedListener {
            if (inConversion) {
                this.inConversion = false
                return@addTextChangedListener
            }

            val amount = it.toString()
            if (amount.isBlank()) {
                return@addTextChangedListener
            }

            val convertedAmount = this.currencyConverterService.convert(
                this.currency2, this.currency1, amount.toDouble()
            )
            this.inConversion = true
            this.setAmount(this.input1, convertedAmount)
        }
    }


    private fun setAmount(input: ConstraintLayout, amount: Double) {
        val numInput = input.findViewById<EditText>(R.id.currency_input_money_amount)
        numInput.setText(String.format("%.2f", amount))
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
        val textView = findViewById<TextView>(R.id.updated_at)

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
