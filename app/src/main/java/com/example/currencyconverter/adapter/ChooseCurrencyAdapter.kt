package com.example.currencyconverter.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.models.Currency

class ChooseCurrencyAdapter(
    private val currencies: List<Currency>,
    private val onCurrencyClickListener: (Currency) -> Unit,
    private val actualSelectedCurrency: Currency,
    private val context: Context
) : RecyclerView.Adapter<ChooseCurrencyAdapter.MyViewHolder>() {

    private val layoutItem = R.layout.item_choose_currency
    private val layoutItemWithDivider = R.layout.item_dzdzqdzq

    open class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ViewHolder(view: View) : MyViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.item_choose_currency_symbol)
        val name: TextView = view.findViewById(R.id.item_choose_currency_name)
        val root: View = view.findViewById(R.id.item_choose_currency_root)
    }

    class ViewHolderNul(view: View) : MyViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == this.layoutItem) {
            ViewHolder(
                View.inflate(parent.context, R.layout.item_choose_currency, null)
            )
        } else {
            ViewHolderNul(
                View.inflate(parent.context, R.layout.item_dzdzqdzq, null)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = this.currencies[position]

        return if (item.code == "EUR") {
            this.layoutItemWithDivider
        } else {
            this.layoutItem
        }
    }

    override fun getItemCount(): Int = this.currencies.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = this.currencies[position]

        if (holder is ViewHolder) {
            // set texts
            holder.symbol.text = item.symbol
            holder.name.text = item.name

            // set click listener
            holder.root.setOnClickListener {
                Log.i("ChooseCurrencyAdapter", "Currency clicked: $item")
                this.onCurrencyClickListener(item)
            }
        }
    }
}
