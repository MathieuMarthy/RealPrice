package com.app.realprice.itemDecorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.realprice.R

class PopularLimiterItemDecoration(
    context: Context,
    private val limit: Int
) : RecyclerView.ItemDecoration() {

    private val divider: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.limiter)

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position == this.limit - 1) {
            val space = 30
            outRect.bottom = space

            // Ajoutez l'espacement en haut du premier élément
            if (position == 0) {
                outRect.top = space
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + 100
        val right = parent.width - parent.paddingRight - 100

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position == this.limit) {
                val previousChild = parent.getChildAt(i - 1)

                val top = previousChild.bottom + 12
                val bottom = child.top - 12

                divider?.setBounds(left, top, right, bottom)
                divider?.draw(c)
                return
            }
        }
    }
}
