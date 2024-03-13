package com.example.currencyconverter.view

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import com.example.currencyconverter.R
import com.example.currencyconverter.services.ConfigurationService

class SettingsActivity : AppCompatActivity() {

    private lateinit var configurationService: ConfigurationService

    private lateinit var inputTaxRate: EditText
    private lateinit var inputTaxRateText: TextView
    private lateinit var inputFixedTax: EditText
    private lateinit var inputFixedTaxText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // init service
        this.configurationService = ConfigurationService(this)

        // get inputs
        val switchAllowMobileData = findViewById<SwitchCompat>(R.id.settings_allow_data_switch)
        val switchActiveBankCharge = findViewById<SwitchCompat>(R.id.settings_bank_taxes_switch)
        this.inputTaxRate = findViewById(R.id.settings_tax_rate_input)
        this.inputFixedTax = findViewById(R.id.settings_fixed_tax_input)
        this.inputTaxRateText = findViewById(R.id.settings_tax_rate_text)
        this.inputFixedTaxText = findViewById(R.id.settings_fixed_tax_text)

        // set value
        switchAllowMobileData.isChecked = this.configurationService.configuration.allowMobileData
        switchActiveBankCharge.isChecked = this.configurationService.configuration.activeBankCharge

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

        this.refreshBankSettingsDisplay()

        // set back button
        val backBtn = findViewById<ImageButton>(R.id.settings_back_button)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun refreshBankSettingsDisplay() {
        // TODO: adapter les couleurs en fonction du thème
        if (this.configurationService.configuration.activeBankCharge) {
            // enable inputs
            this.inputTaxRate.isEnabled = true
            this.inputFixedTax.isEnabled = true

            // set text color
            this.inputTaxRateText.setTextColor(getColor(R.color.true_white))
            this.inputFixedTaxText.setTextColor(getColor(R.color.true_white))

            // set value
            this.inputTaxRate.setText(this.configurationService.configuration.taxRate.toString())
            this.inputFixedTax.setText(this.configurationService.configuration.fixedTax.toString())
        } else {
            // disable inputs
            this.inputTaxRate.isEnabled = false
            this.inputFixedTax.isEnabled = false

            // set text color
            this.inputTaxRateText.setTextColor(getColor(R.color.light_grey))
            this.inputFixedTaxText.setTextColor(getColor(R.color.light_grey))

            // clear value
            this.inputTaxRate.setText("")
            this.inputFixedTax.setText("")
        }
    }
}
