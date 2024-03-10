package com.example.currencyconverter.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.models.Currency

class ChooseCurrencyItemDecorator(
    context: Context,
    private val currencies: List<Currency>,
    private val limit: Currency,
    private val actualSelectedCurrency: Currency
) : RecyclerView.ItemDecoration() {

    private val divider: Drawable? = ContextCompat.getDrawable(context, R.drawable.limiter)
    private val selected: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.selected_currency)

//    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        val left = parent.paddingLeft
//        val right = parent.width - parent.paddingRight
//
//        for (i in 0 until parent.childCount) {
//            val child = parent.getChildAt(i)
//            // Nous récupérons la devise correspondant à l'élément actuel
//            val currency = currencies[parent.getChildAdapterPosition(child)]
//
//            // Nous vérifions si la devise est populaire
//            if (currency == this.limit) { // Vérifiez votre condition ici
//                val params = child.layoutParams as RecyclerView.LayoutParams
//
//                // Nous définissons la position de notre séparateur
//                val top = child.bottom + params.bottomMargin
//                val bottom = top + (divider?.intrinsicHeight ?: 0)
//
//                // Nous définissons les limites de notre séparateur et nous le dessinons
//                divider?.setBounds(left, top, right, bottom)
//                divider?.draw(c)
//            }
//        }
//    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + 40
        val right = parent.width - parent.paddingRight - 40

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            // Nous récupérons la devise correspondant à l'élément actuel
            val currency = currencies[parent.getChildAdapterPosition(child)]

            // Nous vérifions si la devise est populaire
            if (currency == this.actualSelectedCurrency) { // Vérifiez votre condition ici
                val params = child.layoutParams as RecyclerView.LayoutParams

                // Nous définissons la position de notre séparateur
                val top = child.bottom + params.bottomMargin
                val bottom = top + 120

                // Nous définissons les limites de notre séparateur et nous le dessinons
                selected?.setBounds(left, top, right, bottom)
                selected?.draw(c)
            }
        }
    }
}
