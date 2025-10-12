package com.app.realprice.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.realprice.R
import com.app.realprice.adapter.ChooseCurrencyAdapter
import com.app.realprice.itemDecorator.ChooseCurrencyItemDecorator
import com.app.realprice.itemDecorator.PopularLimiterItemDecoration
import com.app.realprice.models.Currency
import com.app.realprice.services.CurrencyCountryService
import com.app.realprice.services.ThemeService

class ChooseCurrencyDialog {

    companion object {
        fun show(
            context: Context,
            currencies: List<Currency>,
            popularSize: Int,
            actualSelectedCurrency: Currency,
            callback: (Currency) -> Unit
        ) {
            val themeService = ThemeService(context)
            val currencyCountryService = CurrencyCountryService(context)

            // setup dialog
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_choose_currency)

            val color = if (themeService.isDarkThemeActive()) {
                context.getColor(R.color.grey)
            } else {
                context.getColor(R.color.true_white)
            }

            val layout = dialog.findViewById<View>(R.id.dialog_root)
            layout.backgroundTintList = android.content.res.ColorStateList.valueOf(color)

            // close button
            val closeBtn =
                dialog.findViewById<ImageButton>(R.id.dialog_choose_currency_close_button)
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            // search functionality
            val searchEditText = dialog.findViewById<EditText>(R.id.dialog_choose_currency_search)
            searchEditText.backgroundTintList = android.content.res.ColorStateList.valueOf(color)

            val originalCurrencies = currencies.toMutableList()
            originalCurrencies.add(0, Currency("", "header", 0.0)) // add header

            val currenciesWithHeader = originalCurrencies.toMutableList()

            val allCurrenciesRecyclerView =
                dialog.findViewById<RecyclerView>(R.id.dialog_choose_currency_rv_all_currencies)
            val allCurrenciesAdapter = ChooseCurrencyAdapter(
                currenciesWithHeader,
                context
            ) {
                dialog.dismiss()
                callback(it)
            }

            // set currencies
            allCurrenciesRecyclerView.addItemDecoration(
                ChooseCurrencyItemDecorator(
                    context,
                    currenciesWithHeader,
                    actualSelectedCurrency
                )
            )

            // add a limiter between popular currencies and all currencies
            val currentPopularSize = popularSize + 1 // +1 because of the header
            allCurrenciesRecyclerView.addItemDecoration(
                PopularLimiterItemDecoration(context, currentPopularSize)
            )

            allCurrenciesRecyclerView.adapter = allCurrenciesAdapter
            allCurrenciesRecyclerView.layoutManager = LinearLayoutManager(context)

            // Add text change listener for search
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()

                    // Clear decorations first
                    while (allCurrenciesRecyclerView.itemDecorationCount > 0) {
                        allCurrenciesRecyclerView.removeItemDecorationAt(0)
                    }

                    if (query.isEmpty()) {
                        // Show original list - reset to initial state
                        val resetList = originalCurrencies.toMutableList()

                        // Re-add decorations for full list
                        allCurrenciesRecyclerView.addItemDecoration(
                            ChooseCurrencyItemDecorator(context, resetList, actualSelectedCurrency)
                        )
                        allCurrenciesRecyclerView.addItemDecoration(
                            PopularLimiterItemDecoration(context, popularSize + 1)
                        )

                        // Update adapter
                        allCurrenciesAdapter.updateCurrencies(resetList)
                    } else {
                        // Recherche complète avec le service (code, nom de devise, et nom de pays)
                        val filteredCurrencies =
                            currencyCountryService.searchCurrencies(query, currencies)

                        // Create new list with header for filtered results
                        val filteredList = mutableListOf<Currency>()
                        filteredList.add(Currency("", "header", 0.0)) // Add header first
                        filteredList.addAll(filteredCurrencies) // Add filtered results

                        // Add decoration for filtered list
                        allCurrenciesRecyclerView.addItemDecoration(
                            ChooseCurrencyItemDecorator(
                                context,
                                filteredList,
                                actualSelectedCurrency
                            )
                        )

                        // Update adapter
                        allCurrenciesAdapter.updateCurrencies(filteredList)
                    }
                }
            })

            dialog.show()

            // change the size of the dialog
            val window = dialog.window
            if (window != null) {
                val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
                val height = (context.resources.displayMetrics.heightPixels * 0.90).toInt()
                window.setLayout(width, height)
                window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }
        }
    }
}
