package com.app.realprice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.realprice.R
import com.app.realprice.models.Currency

class ChooseCurrencyAdapter(
    private var currencies: MutableList<Currency>,
    private val context: android.content.Context,
    private val onCurrencyClickListener: (Currency) -> Unit
) : RecyclerView.Adapter<ChooseCurrencyAdapter.ViewHolder>() {

    private var isSearchMode = false

    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CURRENCY = 1
    }

    fun updateCurrencies(newCurrencies: List<Currency>) {
        this.currencies.clear()
        this.currencies.addAll(newCurrencies)
        notifyDataSetChanged()
    }

    fun setSearchMode(isSearching: Boolean) {
        this.isSearchMode = isSearching
        // Notifier seulement l'en-tête (position 0) pour éviter de redessiner toute la liste
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_header_currency, parent, false)
            HeaderViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_choose_currency, parent, false)
            CurrencyViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_CURRENCY
    }

    override fun getItemCount(): Int = this.currencies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            // Gérer l'affichage de l'en-tête selon le mode
            val headerText = holder.itemView.findViewById<TextView>(R.id.header_text)
            if (isSearchMode) {
                headerText.text = context.getString(R.string.search_results)
            } else {
                headerText.text = context.getString(R.string.popular_currencies)
            }
        } else if (holder is CurrencyViewHolder) {
            val item = this.currencies[position]

            // set texts
            holder.symbol.text = item.symbol
            holder.name.text = item.getName(this.context)

            // currency flag
            val resId = this.context.resources.getIdentifier(
                item.flag,
                "drawable",
                this.context.packageName
            )
            if (resId != 0) {
                holder.flag.setImageResource(resId)
            } else {
                holder.flag.setImageResource(R.drawable.unknow)
            }


            // set click listener
            holder.root.setOnClickListener {
                this.onCurrencyClickListener(item)
            }
        }
    }

    class HeaderViewHolder(view: View) : ViewHolder(view)

    class CurrencyViewHolder(view: View) : ViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.item_choose_currency_symbol)
        val name: TextView = view.findViewById(R.id.item_choose_currency_name)
        val root: View = view.findViewById(R.id.item_choose_currency_root)
        val flag: ImageView = view.findViewById(R.id.item_choose_currency_flag)
    }
}
