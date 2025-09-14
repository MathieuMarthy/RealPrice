package com.app.realprice.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.realprice.R
import com.app.realprice.adapter.ChooseCurrencyAdapter
import com.app.realprice.itemDecorator.ChooseCurrencyItemDecorator
import com.app.realprice.itemDecorator.PopularLimiterItemDecoration
import com.app.realprice.models.Currency
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

            // setup dialog
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_choose_currency)


            val color = if (themeService.isDarkThemeActive()) {
                context.getColor(R.color.grey)
            } else {
                context.getColor(R.color.true_white)
            }

            val layout = dialog.findViewById<View>(R.id.dialog_view)
            layout.setBackgroundColor(color)

            // close button
            val closeBtn =
                dialog.findViewById<ImageButton>(R.id.dialog_choose_currency_close_button)
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            val currenciesWithHeader = currencies.toMutableList()
            currenciesWithHeader.add(
                0,
                Currency(
                    "",
                    "header",
                    0.0,
                )
            ) // add a fake currency as header

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
            allCurrenciesRecyclerView.addItemDecoration(
                PopularLimiterItemDecoration(
                    context,
                    popularSize + 1 // +1 because of the header
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
                window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }
        }
    }
}
