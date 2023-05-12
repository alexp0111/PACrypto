package com.example.pacrypto.adapters

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.data.CurrencyInfo
import com.example.pacrypto.data.room.DBAsset
import com.example.pacrypto.databinding.ItemCoinType1Binding
import kotlin.math.roundToInt

class CurrencyAdapterType1(
    val onItemClicked: (Int, DBAsset) -> Unit
) : RecyclerView.Adapter<CurrencyAdapterType1.MyViewHolder>() {

    private var list: ArrayList<DBAsset> = arrayListOf()

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

    fun deleteItem(i: Int){
        list.removeAt(i)
        notifyItemRemoved(i)
    }

    fun updateList(list: ArrayList<DBAsset>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemCoinType1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asset: DBAsset) {
            binding.tvName.text = asset.name
            binding.tvTicker.text = asset.asset_id
            binding.tvTicker.isSelected = true
            binding.tvValue.text =  String.format("%.2f", asset.price_usd) + "$"
            binding.tvValue.isSelected = true
            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, asset) }
        }
    }
}