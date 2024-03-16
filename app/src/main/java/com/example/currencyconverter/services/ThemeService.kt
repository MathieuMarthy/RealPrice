package com.example.currencyconverter.services

import android.content.Context
import android.content.res.Configuration

class ThemeService(
    private val context: Context
) {

    fun isDarkThemeActive(): Boolean {
        return this.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}
