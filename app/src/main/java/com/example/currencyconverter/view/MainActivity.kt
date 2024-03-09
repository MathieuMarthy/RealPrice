package com.example.currencyconverter.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.dao.ExchangeRateDAO
import com.example.currencyconverter.dao.UpdateDateDAO
import com.example.currencyconverter.services.ApiService
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    private lateinit var updateDateDAO: UpdateDateDAO
    private lateinit var exchangeRateDAO: ExchangeRateDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // init services
        this.apiService = ApiService.getInstance(this)
        this.updateDateDAO = UpdateDateDAO(this)
        this.exchangeRateDAO = ExchangeRateDAO(this)

        this.refreshLastUpdateDate()

        val testButton = findViewById<Button>(R.id.button)
        testButton.setOnClickListener {
            this.apiService.getCurrencyExchangeRate(
                { _ ->
                    // Handle the success response
                    Log.d(":3", "update date")
                    this.refreshLastUpdateDate()
                },
                { error ->
                    // Handle the error response
                })
        }
    }

    private fun refreshLastUpdateDate() {
        val date = this.updateDateDAO.load() ?: return
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val textView = findViewById<TextView>(R.id.testest)
        textView.text = this.getString(R.string.last_update_at, date.format(formatter))
    }
}
