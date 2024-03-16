package com.example.realprice.dao

import android.content.Context

abstract class JsonDatabase<T>(
    private val filePath: String,
    private val context: Context
) : Database<T>(filePath, context) {

    fun save(data: T) {
        val jsonString = this.gson.toJson(data)
        this.context.openFileOutput(this.filePath, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }
}
