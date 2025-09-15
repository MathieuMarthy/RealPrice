package com.app.realprice.models

import android.content.Context

class Currency(
    val code: String,
    val symbol: String,
    val rate: Double,
    val flag: String = ""
) {
    fun getName(context: Context): String {
        try {
            val resourceId =
                context.resources.getIdentifier(this.code, "string", context.packageName)
            return context.getString(resourceId)
        } catch (_: Exception) {
            return this.code
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Currency) {
            return this.code == other.code
        }

        return false
    }
    override fun toString(): String {
        return "Currency(code='$code', symbol='$symbol', rate=$rate, flag='$flag')"
    }
}
