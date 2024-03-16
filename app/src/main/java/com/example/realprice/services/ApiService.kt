package com.example.realprice.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.realprice.CURRENCY_API_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime


class ApiService private constructor(
    context: Context
) {
    private val queue = Volley.newRequestQueue(context)
    private val gson = Gson()

    /**
     * Get the currency exchange rate from the API based on EUR
     * @param successCallback - The callback function to be called when the request is successful
     * @param errorCallback - The callback function to be called when the request is failed
     */
    fun getCurrencyExchangeRate(
        successCallback: (Map<String, Double>, LocalDateTime) -> Unit,
        errorCallback: () -> Unit
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            CURRENCY_API_URL,
            { response ->
                // Convert the response to a map
                val responseMap = this.gson.fromJson<Map<String, Any>>(
                    response,
                    object : TypeToken<Map<String, Any>>() {}.type
                )

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

                // Get exchanges rates
                val currencyExchangeRate = responseMap["rates"] as Map<String, Double>

                successCallback(currencyExchangeRate, lastUpdateDate)
            },
            { error ->
                Log.d("request", error.toString())
                errorCallback()
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
