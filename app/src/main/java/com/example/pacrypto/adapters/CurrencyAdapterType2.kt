package com.example.pacrypto.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.data.CurrencyInfo
import com.example.pacrypto.data.room.DBAsset
import com.example.pacrypto.databinding.ItemCoinType1Binding
import com.example.pacrypto.databinding.ItemCoinType2Binding

class CurrencyAdapterType2(
    val onItemClicked: (Int, DBAsset) -> Unit
) : RecyclerView.Adapter<CurrencyAdapterType2.MyViewHolder>() {

    private var list: ArrayList<DBAsset> = arrayListOf()

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

    fun updateList(list: ArrayList<DBAsset>) {
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
        fun bind(asset: DBAsset) {
            binding.tvName.text = asset.name
            binding.tvTicker.text = asset.asset_id
            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, asset) }
        }
    }
}