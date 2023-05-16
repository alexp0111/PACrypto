package com.example.pacrypto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.R
import com.example.pacrypto.data.SearchItem
import com.example.pacrypto.databinding.ItemCoinType1Binding

class CoinAdapter(
    val context: Context,
    val onItemClicked: (Int, SearchItem) -> Unit
) : RecyclerView.Adapter<CoinAdapter.MyViewHolder>() {

    private var searchItemList: ArrayList<SearchItem> = arrayListOf()
    private var rateMarker: String = "$"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoinAdapter.MyViewHolder {
        val itemView =
            ItemCoinType1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = searchItemList[position]
        holder.bind(item)
    }

    fun deleteItem(i: Int) {
        searchItemList.removeAt(i)
        notifyItemRemoved(i)
    }

    fun updateList(searchItemList: ArrayList<SearchItem>) {
        val tmpList =
            if (rateMarker == "$") searchItemList.sortedWith(compareBy(nullsFirst()) { it.rateCurrentUSD }).reversed()
            else searchItemList.sortedWith(compareBy(nullsLast()) { it.rateCurrentRUB })
        this.searchItemList = ArrayList(tmpList)
        notifyDataSetChanged()
    }

    fun setRateMarker(rateMarker: String) {
        this.rateMarker = rateMarker
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return searchItemList.size
    }

    inner class MyViewHolder(private val binding: ItemCoinType1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem) {
            binding.tvName.text = item.name
            binding.tvTicker.text = item.ticker
            binding.tvTicker.isSelected = true
            if (item.rateCurrentUSD == null || item.rateCurrentRUB == null || item.timeUpdate == null || item.percents == null) {
                binding.tvValue.text = "Нет данных"
                binding.tvUpdateDate.text = "-"
                binding.tvPercents.text = "-"

                binding.clEnd.visibility = View.GONE
            } else {
                binding.clEnd.visibility = View.VISIBLE

                if (rateMarker == "$"){
                    binding.tvValue.text = buildString {
                        append(String.format("%.2f", item.rateCurrentUSD))
                        append(rateMarker)
                    }
                } else {
                    binding.tvValue.text = buildString {
                        append(String.format("%.2f", item.rateCurrentRUB))
                        append(rateMarker)
                    }
                }
                binding.tvUpdateDate.text = item.timeUpdate
                if (item.percents!!.startsWith("+")) {
                    binding.clEnd.background =
                        ContextCompat.getDrawable(context, R.color.green_300)
                } else {
                    binding.clEnd.background =
                        ContextCompat.getDrawable(context, R.color.orange_300)
                }
                binding.tvPercents.text = item.percents
            }
            binding.tvValue.isSelected = true
            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, item) }
        }
    }
}
