package com.example.pacrypto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.data.CurrencyInfo
import com.example.pacrypto.databinding.ItemCoinType1Binding
import com.example.pacrypto.databinding.ItemCoinType2Binding

class CurrencyAdapterType2(
    val onItemClicked: (Int, CurrencyInfo) -> Unit
) : RecyclerView.Adapter<CurrencyAdapterType2.MyViewHolder>() {

    private var list: ArrayList<CurrencyInfo> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyAdapterType2.MyViewHolder {
        val itemView =
            ItemCoinType2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CurrencyAdapterType2.MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: ArrayList<CurrencyInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun deleteItem(i: Int){
        list.removeAt(i)
        notifyItemRemoved(i)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemCoinType2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currencyInfo: CurrencyInfo) {
            binding.tvName.text = currencyInfo.name
            binding.tvTicker.text = currencyInfo.ticker
            binding.card.setOnClickListener { onItemClicked.invoke(adapterPosition, currencyInfo) }
        }
    }
}