package com.example.pacrypto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.data.CurrencyInfo
import com.example.pacrypto.databinding.ItemCoinType1Binding

class CurrencyAdapterType1(
    val onItemClicked: (Int, CurrencyInfo) -> Unit
) : RecyclerView.Adapter<CurrencyAdapterType1.MyViewHolder>() {

    private var list: List<CurrencyInfo> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyAdapterType1.MyViewHolder {
        val itemView =
            ItemCoinType1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CurrencyAdapterType1.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: List<CurrencyInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemCoinType1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currencyInfo: CurrencyInfo) {
            binding.tvName.text = currencyInfo.name
            binding.tvTicker.text = currencyInfo.ticker
            binding.card.setOnClickListener { onItemClicked.invoke(adapterPosition, currencyInfo) }
        }
    }
}