package com.app.realprice.services

import android.content.Context
import com.app.realprice.models.Currency
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.Normalizer
import java.util.Locale

class CurrencyCountryService(private val context: Context) {

    private var currencyToCountries: Map<String, List<String>> = emptyMap()
    private var countryToCountryName: Map<String, String> = emptyMap()

    init {
        loadCurrencyCountryMapping()
        loadCountryNames()
    }

    /**
     * Normalize text by removing accents and converting to lowercase
     */
    private fun normalizeText(text: String): String {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
            .lowercase()
    }

    private fun loadCurrencyCountryMapping() {
        try {
            val inputStream = context.assets.open("databases/currencies_to_country.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(json)

            val tempMap = mutableMapOf<String, List<String>>()
            jsonObject.keys().forEach { currencyCode ->
                try {
                    val countriesValue = jsonObject.get(currencyCode)
                    val countries = mutableListOf<String>()

                    when (countriesValue) {
                        is JSONArray -> {
                            // Si c'est un tableau, on traite normalement
                            for (i in 0 until countriesValue.length()) {
                                val country = countriesValue.getString(i)
                                if (country.isNotBlank()) {
                                    countries.add(country)
                                }
                            }
                        }

                        is String -> {
                            // Si c'est une chaîne simple, on l'ajoute si elle n'est pas vide
                            if (countriesValue.isNotBlank()) {
                                countries.add(countriesValue)
                            }
                        }
                    }

                    if (countries.isNotEmpty()) {
                        tempMap[currencyCode] = countries
                    }
                } catch (e: Exception) {
                    // Ignorer les entrées problématiques
                    e.printStackTrace()
                }
            }
            currencyToCountries = tempMap
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadCountryNames() {
        // Mapping des codes pays vers les noms de pays
        val tempMap = mutableMapOf<String, String>()

        // Utilisation des codes ISO pour obtenir les noms des pays
        val availableLocales = Locale.getAvailableLocales()
        availableLocales.forEach { locale ->
            if (locale.country.isNotEmpty()) {
                val countryName = locale.getDisplayCountry(Locale.getDefault())
                if (countryName.isNotEmpty()) {
                    tempMap[locale.country] = countryName
                }
            }
        }

        countryToCountryName = tempMap
    }

    fun searchCurrencies(query: String, allCurrencies: List<Currency>): List<Currency> {
        if (query.isBlank()) {
            return allCurrencies
        }

        val normalizedQuery = normalizeText(query.trim())
        val filteredCurrencies = mutableSetOf<Currency>()

        allCurrencies.forEach { currency ->
            // Search by currency code
            if (normalizeText(currency.code).contains(normalizedQuery)) {
                filteredCurrencies.add(currency)
            }

            // Search by currency name
            val currencyName = normalizeText(currency.getName(context))
            if (currencyName.contains(normalizedQuery)) {
                filteredCurrencies.add(currency)
            }

            // Search by country name
            val countries = currencyToCountries[currency.code] ?: emptyList()
            countries.forEach { countryCode ->
                val countryName = normalizeText(countryToCountryName[countryCode] ?: "")
                if (countryName.contains(normalizedQuery)) {
                    filteredCurrencies.add(currency)
                }
            }
        }

        return filteredCurrencies.toList()
    }
}
