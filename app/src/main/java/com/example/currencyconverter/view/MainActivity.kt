package com.example.currencyconverter.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.adapter.ChooseCurrencyAdapter
import com.example.currencyconverter.itemDecorator.ChooseCurrencyItemDecorator
import com.example.currencyconverter.itemDecorator.PopularLimiterItemDecoration
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

    private var inConversion = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // init
        this.currencyManagerDBService = CurrencyManagerDBService(this)
        this.configurationService = ConfigurationService(this)

        this.input1 = findViewById(R.id.currency_input_1)
        this.input2 = findViewById(R.id.currency_input_2)

        this.refreshLastUpdateDate()

        if (this.isNetworkAvailable() && !this.alreadyUpdateToday()) {
            // update the currency exchange rate if it's not already updated today
            this.currencyManagerDBService.updateExchangeRate {
                this.currencyManagerDBService = CurrencyManagerDBService(this)
                this.init()
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

    private fun refreshOfflineIndication() {
        val noInternetText = findViewById<TextView>(R.id.no_internet_text)
        val noInternetIcon = findViewById<ImageView>(R.id.no_internet_icon)

        val visibility = if (!this.isNetworkAvailable()) View.VISIBLE else View.GONE

        noInternetText.visibility = visibility
        noInternetIcon.visibility = visibility
    }

    private fun convertMoney(fromFristCurrency: Boolean) {
        val amount = when (fromFristCurrency) {
            true -> this.input1.findViewById<EditText>(R.id.currency_input_money_amount).text.toString()
            false -> this.input2.findViewById<EditText>(R.id.currency_input_money_amount).text.toString()
        }

        if (amount.isBlank()) {
            return
        }

        this.inConversion = true

        if (fromFristCurrency) {
            val convertedAmount = this.currencyConverterService.convert(
                this.currency1, this.currency2, amount.toDouble()
            )
            this.setAmount(this.input2, convertedAmount)
        } else {
            val convertedAmount = this.currencyConverterService.convert(
                this.currency2, this.currency1, amount.toDouble()
            )
            this.setAmount(this.input1, convertedAmount)
        }
    }

    private fun openCurrencyDialog(input: ConstraintLayout, actualSelectedCurrency: Currency) {
        // setup dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_choose_currency)

        // close button
        val closeBtn = dialog.findViewById<ImageButton>(R.id.dialog_choose_currency_close_button)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        // Adapter
        val currencies =
            this.currencyManagerDBService.getPopularCurrencies() + this.currencyManagerDBService.currencies

        val allCurrenciesRecyclerView =
            dialog.findViewById<RecyclerView>(R.id.dialog_choose_currency_rv_all_currencies)
        val allCurrenciesAdapter = ChooseCurrencyAdapter(
            currencies
        ) {
            this.setCurrency(input, it)
            dialog.dismiss()
        }

        allCurrenciesRecyclerView.addItemDecoration(
            ChooseCurrencyItemDecorator(
                this,
                currencies,
                actualSelectedCurrency
            )
        )
        allCurrenciesRecyclerView.addItemDecoration(
            PopularLimiterItemDecoration(
                this,
                this.currencyManagerDBService.getPopularCurrencies().size
            )
        )
        allCurrenciesRecyclerView.adapter = allCurrenciesAdapter
        allCurrenciesRecyclerView.layoutManager = LinearLayoutManager(this)

        dialog.show()

        // change the size of the dialog
        val window = dialog.window
        if (window != null) {
            val width =
                (resources.displayMetrics.widthPixels * 0.85).toInt() // 85% de la largeur de l'écran
            val height =
                (resources.displayMetrics.heightPixels * 0.90).toInt() // 90% de la hauteur de l'écran
            window.setLayout(width, height)

            // Définir le fond de la fenêtre de dialogue sur transparent
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null
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
        this.configurationService.refresh()
        this.refreshOfflineIndication()
    }
}
