package com.example.currencyconverter.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import com.example.currencyconverter.R
import com.example.currencyconverter.dialogs.ChooseCurrencyDialog
import com.example.currencyconverter.services.ConfigurationService
import com.example.currencyconverter.services.CurrencyManagerDBService

class SettingsActivity : AppCompatActivity() {

    private lateinit var configurationService: ConfigurationService
    private lateinit var currencyManagerDBService: CurrencyManagerDBService

    private lateinit var inputTaxRate: EditText
    private lateinit var inputTaxRateText: TextView
    private lateinit var inputPourcentageText: TextView
    private lateinit var inputFixedTax: EditText
    private lateinit var inputFixedTaxText: TextView
    private lateinit var inputTaxCurrency: TextView
    private lateinit var buttonTaxCurrency: Button
    private lateinit var inputTaxText: TextView
    private lateinit var inputlimitTax: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // init service
        this.configurationService = ConfigurationService(this)
        this.currencyManagerDBService = CurrencyManagerDBService(this)

        // get inputs
        val switchAllowMobileData = findViewById<SwitchCompat>(R.id.settings_allow_data_switch)
        val switchActiveBankCharge = findViewById<SwitchCompat>(R.id.settings_bank_taxes_switch)
        this.inputTaxRate = findViewById(R.id.settings_tax_rate_input)
        this.inputFixedTax = findViewById(R.id.settings_fixed_tax_input)
        this.inputPourcentageText = findViewById(R.id.settings_pourcentage_text)
        this.inputTaxRateText = findViewById(R.id.settings_tax_rate_text)
        this.inputFixedTaxText = findViewById(R.id.settings_fixed_tax_text)
        this.inputTaxCurrency = findViewById(R.id.settings_tax_currency_text)
        this.buttonTaxCurrency = findViewById(R.id.settings_tax_currency_button)
        this.inputTaxText = findViewById(R.id.settings_limit_tax_text)
        this.inputlimitTax = findViewById(R.id.settings_limit_tax_input)

        // set value
        switchAllowMobileData.isChecked = this.configurationService.configuration.allowMobileData
        switchActiveBankCharge.isChecked = this.configurationService.configuration.activeBankCharge
        this.buttonTaxCurrency.text = this.currencyManagerDBService.getCurrencyByCode(
            this.configurationService.configuration.taxCurrency
        )!!.symbol
        this.inputTaxRate.setText(this.configurationService.configuration.taxRate.toString())
        this.inputFixedTax.setText(this.configurationService.configuration.fixedTax.toString())
        this.inputlimitTax.setText(this.configurationService.configuration.limitTax.toString())

        // set listener
        switchAllowMobileData.setOnCheckedChangeListener { _, isChecked ->
            this.configurationService.setAllowMobileData(isChecked)
        }

        switchActiveBankCharge.setOnCheckedChangeListener { _, isChecked ->
            this.configurationService.setBankCharge(isChecked)
            this.refreshBankSettingsDisplay()
        }

        this.inputTaxRate.addTextChangedListener {
            val taxRate = this.inputTaxRate.text.toString().toDoubleOrNull() ?: 0.0
            this.configurationService.setTaxRate(taxRate)
        }

        this.inputFixedTax.addTextChangedListener {
            val fixedTax = this.inputFixedTax.text.toString().toDoubleOrNull() ?: 0.0
            this.configurationService.setFixedTax(fixedTax)
        }

        this.inputlimitTax.addTextChangedListener {
            val limitTax = this.inputlimitTax.text.toString().toDoubleOrNull() ?: 0.0
            this.configurationService.setLimitTax(limitTax)
        }

        buttonTaxCurrency.setOnClickListener {
            val currencies =
                this.currencyManagerDBService.getPopularCurrencies() + this.currencyManagerDBService.currencies
            val popularSize = this.currencyManagerDBService.getPopularCurrencies().size
            val taxCurrency =
                this.currencyManagerDBService.getCurrencyByCode(this.configurationService.configuration.taxCurrency)!!

            ChooseCurrencyDialog.show(
                this,
                currencies,
                popularSize,
                taxCurrency
            ) { currency ->
                this.configurationService.setTaxCurrency(currency.code)
                this.buttonTaxCurrency.text = currency.symbol
                this.refreshBankSettingsDisplay()
            }
        }

        this.inputlimitTax

        this.refreshBankSettingsDisplay()

        // set back button
        val backBtn = findViewById<ImageButton>(R.id.settings_back_button)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun refreshBankSettingsDisplay() {
        val isEnable: Boolean
        val textColor: Int

        // TODO: adapter les couleurs en fonction du thème
        if (this.configurationService.configuration.activeBankCharge) {
            isEnable = true
            textColor = getColor(R.color.true_white)
        } else {
            isEnable = false
            textColor = getColor(R.color.light_grey)
        }

        this.inputTaxRate.isEnabled = isEnable
        this.inputFixedTax.isEnabled = isEnable
        this.buttonTaxCurrency.isEnabled = isEnable
        this.inputlimitTax.isEnabled = isEnable

        this.inputTaxRate.setTextColor(textColor)
        this.inputFixedTax.setTextColor(textColor)
        this.inputTaxRateText.setTextColor(textColor)
        this.inputFixedTaxText.setTextColor(textColor)
        this.inputTaxCurrency.setTextColor(textColor)
        this.inputPourcentageText.setTextColor(textColor)
        this.inputTaxText.setTextColor(textColor)
    }
}
