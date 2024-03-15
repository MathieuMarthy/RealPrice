package com.example.currencyconverter.itemDecorator

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
    private val actualSelectedCurrency: Currency
) : RecyclerView.ItemDecoration() {

    private val selected: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.selected_currency)

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + 40
        val right = parent.width - parent.paddingRight - 40

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            // We retrieve the currency corresponding to the current item
            val currency = currencies[parent.getChildAdapterPosition(child)]

            // We check if the currency is popular
            if (currency == this.actualSelectedCurrency) {
                // We define the position of our separator
                val top = child.bottom - 125
                val bottom = child.bottom - 5

                // We define the boundaries of our separator and we draw it
                selected?.setBounds(left, top, right, bottom)
                selected?.draw(c)
            }
        }
    }
}
