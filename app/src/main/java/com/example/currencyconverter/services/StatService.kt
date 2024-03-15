package com.example.currencyconverter.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.currencyconverter.WEBHOOK_API_URL
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest
import android.os.Build
import java.util.Locale


class StatService private constructor(context: Context) {

    private val queue = Volley.newRequestQueue(context)

    fun sendStat() {
        // récupérer les informations du téléphone
        val osVersion = Build.VERSION.RELEASE // Version de l'OS
        val deviceModel = Build.MODEL // Modèle de l'appareil
        val deviceManufacturer = Build.MANUFACTURER // Fabricant de l'appareil
        val deviceLanguage = Locale.getDefault().language // Langue par défaut du système
        // créer le corp JSON à envoyer
        val jsonBody = JSONObject()
        jsonBody.put("content", "os:$osVersion;model:$deviceModel;maker:$deviceManufacturer;language:$deviceLanguage")
        // créer la requête POST du Webhook
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, WEBHOOK_API_URL, jsonBody, Response.Listener {}, Response.ErrorListener {})
        // envoyer la requête
        queue.add(jsonObjectRequest)
    }

    companion object {
        @Volatile
        private var INSTANCE: StatService? = null

        /**
         * Get the instance of the ApiService
         * @return ApiService - The instance of the StatService
         */
        fun getInstance(context: Context): StatService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StatService(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}
