package com.example.currencyconverter.view

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.example.currencyconverter.R
import com.example.currencyconverter.dialogs.ChooseCurrencyDialog
import com.example.currencyconverter.models.Currency
import com.example.currencyconverter.services.ConfigurationService
import com.example.currencyconverter.services.CurrencyConverterService
import com.example.currencyconverter.services.CurrencyManagerDBService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private lateinit var currencyConverterService: CurrencyConverterService
    private lateinit var currencyManagerDBService: CurrencyManagerDBService
    private lateinit var configurationService: ConfigurationService

    private lateinit var currency1: Currency
    private lateinit var currency2: Currency

    private lateinit var input1: ConstraintLayout
    private lateinit var input2: ConstraintLayout
    private lateinit var taxesText: TextView

    private var inConversion = false
    private var requestInProcess = false
    private var lastConvertFromFirstCurrency = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // init
        this.currencyManagerDBService = CurrencyManagerDBService(this)
        this.configurationService = ConfigurationService(this)

        this.input1 = findViewById(R.id.currency_input_1)
        this.input2 = findViewById(R.id.currency_input_2)
        this.taxesText = findViewById(R.id.taxes_amount_text)

        this.refreshLastUpdateDate()

        if (this.isNetworkAvailable() && !this.alreadyUpdateToday()) {
            // update the currency exchange rate if it's not already updated today
            this.requestInProcess = true
            this.currencyManagerDBService.updateExchangeRate {
                this.currencyManagerDBService = CurrencyManagerDBService(this)
                this.init()
                this.requestInProcess = false
            }
        } else {
            this.init()
        }
    }

    private fun init() {
        // set the default currency
        this.currency1 = this.currencyManagerDBService.getCurrencyByCode(
            this.configurationService.configuration.defaultCurrency1
        )!!
        this.currency2 = this.currencyManagerDBService.getCurrencyByCode(
            this.configurationService.configuration.defaultCurrency2
        )!!

        // set the currency on the UI
        this.setCurrency(this.input1, this.currency1)
        this.setCurrency(this.input2, this.currency2)

        // set converters listeners
        val numInput1 = this.input1.findViewById<EditText>(R.id.currency_input_money_amount)
        val numInput2 = this.input2.findViewById<EditText>(R.id.currency_input_money_amount)

        numInput1.addTextChangedListener {
            if (inConversion) {
                this.inConversion = false
                return@addTextChangedListener
            }

            this.convertMoney(true)
        }

        numInput2.addTextChangedListener {
            if (inConversion) {
                this.inConversion = false
                return@addTextChangedListener
            }

            this.convertMoney(false)
        }

        // set currency listeners
        val currencyButton1 = this.input1.findViewById<Button>(R.id.currency_input_dropDown_curreny)
        val currencyButton2 = this.input2.findViewById<Button>(R.id.currency_input_dropDown_curreny)

        currencyButton1.setOnClickListener {
            this.openCurrencyDialog(this.input1, this.currency1)
        }

        currencyButton2.setOnClickListener {
            this.openCurrencyDialog(this.input2, this.currency2)
        }

        // settings button
        val settingBtn = findViewById<View>(R.id.settings)
        settingBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        this.refreshOfflineIndication()
        this.initConverterAndRefreshDate()
    }


    private fun exchangeCurrenciesAndAmount() {
        val tempCurrency = this.currency1
        val tempAmount = this.input1
            .findViewById<EditText>(R.id.currency_input_money_amount).text
            .toString().toDoubleOrNull() ?: 0.0

        this.setCurrency(this.input1, this.currency2)
        this.setAmount(
            this.input1, this.input2
                .findViewById<EditText>(R.id.currency_input_money_amount).text
                .toString().toDoubleOrNull() ?: 0.0
        )

        this.setCurrency(this.input2, tempCurrency)
        this.setAmount(this.input2, tempAmount)
    }

    private fun refreshOfflineIndication() {
        val noInternetText = findViewById<TextView>(R.id.no_internet_text)
        val noInternetIcon = findViewById<ImageView>(R.id.no_internet_icon)

        val visibility = if (!this.isNetworkAvailable()) View.VISIBLE else View.GONE

        noInternetText.visibility = visibility
        noInternetIcon.visibility = visibility
    }

    private fun convertMoney(fromFristCurrency: Boolean) {
        this.lastConvertFromFirstCurrency = fromFristCurrency
        var amount = when (fromFristCurrency) {
            true -> this.input1.findViewById<EditText>(R.id.currency_input_money_amount).text.toString()
            false -> this.input2.findViewById<EditText>(R.id.currency_input_money_amount).text.toString()
        }

        if (amount.isBlank()) {
            return
        }
        amount = amount.replace(",", ".")

        this.inConversion = true

        if (fromFristCurrency) {
            val (convertedAmount, taxesAmount) = this.currencyConverterService.convert(
                this.currency1, this.currency2, amount.toDouble()
            )
            this.setAmount(this.input2, convertedAmount)
            this.setTaxesText(taxesAmount, convertedAmount, this.currency2)
        } else {
            val (convertedAmount, taxesAmount) = this.currencyConverterService.convert(
                this.currency2, this.currency1, amount.toDouble()
            )
            this.setAmount(this.input1, convertedAmount)
            this.setTaxesText(taxesAmount, convertedAmount, this.currency2)

        }
    }

    private fun setTaxesText(taxesAmount: Double, total: Double, currency: Currency) {
        if (taxesAmount == 0.0) {
            this.taxesText.text = ""
            return
        }

        val taxesString = String.format("%.2f", taxesAmount)
        val totalString = String.format("%.2f", total + taxesAmount)
        val taxesAmountSymbol = "$taxesString ${currency.symbol}"
        val totalAmountSymbol = "$totalString ${currency.symbol}"

        this.taxesText.text = getString(
            R.string.taxes_amount_text,
            taxesAmountSymbol,
            totalAmountSymbol
        )
    }

    private fun openCurrencyDialog(input: ConstraintLayout, actualSelectedCurrency: Currency) {
        val currencies =
            this.currencyManagerDBService.getPopularCurrencies() + this.currencyManagerDBService.currencies
        val popularSize = this.currencyManagerDBService.getPopularCurrencies().size

        ChooseCurrencyDialog.show(
            this,
            currencies,
            popularSize,
            actualSelectedCurrency
        ) { currency ->
            when (input) {
                this.input1 -> {
                    if (currency == this.currency2) {
                        this.exchangeCurrenciesAndAmount()
                    } else {
                        this.setCurrency(input, currency)
                    }
                }

                this.input2 -> {
                    if (currency == this.currency1) {
                        this.exchangeCurrenciesAndAmount()
                    } else {
                        this.setCurrency(input, currency)
                    }
                }
            }
        }
    }

    private fun setAmount(input: ConstraintLayout, amount: Double) {
        val numInput = input.findViewById<EditText>(R.id.currency_input_money_amount)
        numInput.setText(String.format("%.2f", amount))
    }

    private fun setCurrency(input: ConstraintLayout, currency: Currency) {
        when (input) {
            this.input1 -> {
                this.currency1 = currency
                this.convertMoney(false)
                this.configurationService.saveCurrency1(currency.code)
            }

            this.input2 -> {
                this.currency2 = currency
                this.convertMoney(true)
                this.configurationService.saveCurrency2(currency.code)
            }
        }

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
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        // check if wifi or ethernet is available
        if (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        ) {
            return true
        }

        // check if mobile data is available and if it's allowed in the settings
        if (
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) &&
            this.configurationService.configuration.allowMobileData
        ) {
            return true
        }

        return false
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

    override fun onResume() {
        super.onResume()

        if (!this.requestInProcess) {
            this.configurationService.refresh()
            this.currencyConverterService.refreshConfig()
            this.refreshOfflineIndication()
            this.convertMoney(this.lastConvertFromFirstCurrency)
        }
    }
}
