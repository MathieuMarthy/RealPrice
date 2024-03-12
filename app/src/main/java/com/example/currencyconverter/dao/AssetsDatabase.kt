package com.example.currencyconverter.dao

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class AssetsDatabase<T>(
    private val filePath: String,
    private val context: Context
) {
    private val gson = Gson()

    fun load(): T? {
        return try {
            val json = this.context.assets.open(this.filePath).bufferedReader().use {
                it.readText()
            }
            this.gson.fromJson<T>(json, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            Log.e("Database", "Error reading file: $filePath", e)
            null
        }
    }
}
