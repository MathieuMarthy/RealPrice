package com.example.currencyconverter.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.models.Currency

class ChooseCurrencyAdapter(
    private val currencies: List<Currency>,
    private val onCurrencyClickListener: (Currency) -> Unit
) : RecyclerView.Adapter<ChooseCurrencyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val symbol: TextView = view.findViewById(R.id.item_choose_currency_symbol)
        val name: TextView = view.findViewById(R.id.item_choose_currency_name)
        val root: View = view.findViewById(R.id.item_choose_currency_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.item_choose_currency, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.currencies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = this.currencies[position]

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
