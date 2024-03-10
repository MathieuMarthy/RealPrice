package com.example.currencyconverter.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.currencyconverter.CURRENCY_API_URL
import com.example.currencyconverter.dao.ExchangeRateDAO
import com.example.currencyconverter.dao.UpdateDateDAO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime


class ApiService private constructor(
    context: Context
) {
    private val queue = Volley.newRequestQueue(context)
    private val gson = Gson()

    private val exchangeRateDAO = ExchangeRateDAO(context)
    private val updateDateDAO = UpdateDateDAO(context)

    /**
     * Get the currency exchange rate from the API based on EUR
     * @param successCallback - The callback function to be called when the request is successful
     * @param errorCallback - The callback function to be called when the request is failed
     */
    fun getCurrencyExchangeRate(
        successCallback: (Map<String, Double>) -> Unit,
        errorCallback: () -> Unit
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            CURRENCY_API_URL,
            { response ->
                Log.d(":3", "getCurrencyExchangeRate")
                val responseMap = this.gson.fromJson<Map<String, Any>>(
                    response,
                    object : TypeToken<Map<String, Any>>() {}.type
                )
                Log.d(":3", responseMap.toString())

                // If the response is not successful
                if (responseMap.getOrDefault("result", "success") != "success" ||
                    !responseMap.containsKey("rates")
                ) {
                    errorCallback()
                    return@StringRequest
                }

                // Get the last update date
                val lastUpdateTimeStamp = responseMap["time_last_update_unix"] as Double
                val lastUpdateDate = LocalDateTime.ofEpochSecond(
                    lastUpdateTimeStamp.toLong(),
                    0,
                    java.time.ZoneOffset.UTC
                )

                val currencyExchangeRate = responseMap["rates"] as Map<String, Double>

                // Save the currency exchange rate and the last update date
                this.exchangeRateDAO.save(currencyExchangeRate)
                this.updateDateDAO.save(lastUpdateDate)

                successCallback(currencyExchangeRate)
            },
            { error ->
                Log.d("request", error.toString())
            }
        )

        this.queue.add(stringRequest)
    }

    companion object {
        @Volatile
        private var INSTANCE: ApiService? = null

        /**
         * Get the instance of the ApiService
         * @return ApiService - The instance of the ApiService
         */
        fun getInstance(context: Context): ApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiService(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}
