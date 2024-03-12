package com.example.currencyconverter.dao

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

abstract class Database<T>(
    private val filePath: String,
    private val context: Context
) {
    protected val gson = Gson()


    open fun load(typeToken: Type = object : TypeToken<T>() {}.type): T? {
        return try {
            val jsonString = this.context.openFileInput(this.filePath).bufferedReader().use {
                it.readText()
            }
            val data = this.gson.fromJson<T>(jsonString, typeToken)
            data
        } catch (e: Exception) {
            Log.e("Database", "Error reading file: $filePath", e)
            null
        }
    }
}
