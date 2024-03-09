package com.example.currencyconverter.dao

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

abstract class JsonDatabase<T>(
    private val filePath: String,
    private val context: Context
) {
    private val gson = Gson()

    fun save(data: T) {
        val jsonString = this.gson.toJson(data)
        this.context.openFileOutput(this.filePath, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
        Log.i("JsonDatabase", "Data saved to $filePath")
    }

    fun load(): T? {
        Log.i("JsonDatabase", "Data loaded from $filePath")

        return try {
            val jsonString = this.context.openFileInput(this.filePath).bufferedReader().use {
                it.readText()
            }
            val data = this.gson.fromJson<T>(jsonString, object : TypeToken<T>() {}.type)
            Log.i("JsonDatabase", "Data loaded from $filePath")

            data
        } catch (e: Exception) {
            Log.i("JsonDatabase", "Failed to load data from $filePath")
            null
        }
    }
}
