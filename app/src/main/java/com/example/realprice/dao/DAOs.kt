package com.example.realprice.dao

import android.content.Context
import com.example.realprice.models.Configuration
import java.time.LocalDateTime
import java.time.ZoneOffset


class ExchangeRateDAO(context: Context) :
    JsonDatabase<Map<String, Double>>("exchange_rate.json", context)


class CurrencyInfoDAO(context: Context) :
    AssetsDatabase<List<Map<String, String>>>("databases/currency_info.json", context)


class ConfigurationDAO(context: Context) :
    JsonDatabase<Configuration>("configuration.json", context)


class UpdateDateDAO(context: Context) {
    private val keyUpdateDate = "updateDate"
    private val sharedPref = context.getSharedPreferences("updateDate_prefs", Context.MODE_PRIVATE)

    /**
     * Save the last update date in the shared preferences
     * @param localDateTime - The date to be saved
     */
    fun save(localDateTime: LocalDateTime) {
        with(sharedPref.edit()) {
            putLong(keyUpdateDate, localDateTime.toEpochSecond(ZoneOffset.UTC))
            apply()
        }
    }

    /**
     * Load the last update date from the shared preferences
     * @return The last update date
     */
    fun load(): LocalDateTime? {
        val epochSecond = sharedPref.getLong(this.keyUpdateDate, -1)
        return if (epochSecond == -1L) {
            null
        } else {
            LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)
        }
    }
}
