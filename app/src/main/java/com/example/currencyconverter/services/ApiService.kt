package com.example.currencyconverter.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.currencyconverter.CURRENCY_API_URL


class ApiService private constructor(
    context: Context
) {
    private val queue = Volley.newRequestQueue(context)

    /**
     * Get the currency exchange rate from the API based on EUR
     * @param successCallback - The callback function to be called when the request is successful
     * @param errorCallback - The callback function to be called when the request is failed
     */
    fun getCurrencyExchangeRate(
        successCallback: (Map<String, Int>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            CURRENCY_API_URL,
            { response ->
                Log.d("request", response)
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
