package com.example.currencyconverter.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.services.ApiService

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        this.apiService = ApiService.getInstance(this)

        val testButton = findViewById<Button>(R.id.button)
        testButton.setOnClickListener {
            this.apiService.getCurrencyExchangeRate(
                { response ->
                    // Handle the success response
                },
                { error ->
                    // Handle the error response
                })
        }
    }
}
