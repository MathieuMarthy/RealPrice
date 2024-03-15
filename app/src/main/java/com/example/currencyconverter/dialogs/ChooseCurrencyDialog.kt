package com.example.currencyconverter.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.adapter.ChooseCurrencyAdapter
import com.example.currencyconverter.itemDecorator.ChooseCurrencyItemDecorator
import com.example.currencyconverter.itemDecorator.PopularLimiterItemDecoration
import com.example.currencyconverter.models.Currency

class ChooseCurrencyDialog {

    companion object {
        fun show(
            context: Context,
            currencies: List<Currency>,
            popularSize: Int,
            actualSelectedCurrency: Currency,
            callback: (Currency) -> Unit
        ) {
            // setup dialog
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_choose_currency)

            // close button
            val closeBtn =
                dialog.findViewById<ImageButton>(R.id.dialog_choose_currency_close_button)
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            val allCurrenciesRecyclerView =
                dialog.findViewById<RecyclerView>(R.id.dialog_choose_currency_rv_all_currencies)
            val allCurrenciesAdapter = ChooseCurrencyAdapter(
                currencies
            ) {
                dialog.dismiss()
                callback(it)
            }

            allCurrenciesRecyclerView.addItemDecoration(
                ChooseCurrencyItemDecorator(
                    context,
                    currencies,
                    actualSelectedCurrency
                )
            )
            allCurrenciesRecyclerView.addItemDecoration(
                PopularLimiterItemDecoration(
                    context,
                    popularSize
                )
            )
            allCurrenciesRecyclerView.adapter = allCurrenciesAdapter
            allCurrenciesRecyclerView.layoutManager = LinearLayoutManager(context)

            dialog.show()

            // change the size of the dialog
            val window = dialog.window
            if (window != null) {
                val width =
                    (context.resources.displayMetrics.widthPixels * 0.85).toInt() // 85% de la largeur de l'écran
                val height =
                    (context.resources.displayMetrics.heightPixels * 0.90).toInt() // 90% de la hauteur de l'écran
                window.setLayout(width, height)

                // Définir le fond de la fenêtre de dialogue sur transparent
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }
}
