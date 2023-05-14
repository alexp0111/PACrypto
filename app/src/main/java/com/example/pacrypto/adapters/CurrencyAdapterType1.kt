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

class CurrencyAdapterType1(
    val context: Context,
    val onItemClicked: (Int, SearchItem) -> Unit
) : RecyclerView.Adapter<CurrencyAdapterType1.MyViewHolder>() {

    private var searchItemList: ArrayList<SearchItem> = arrayListOf()
    private var rateMarker: String = "$"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyAdapterType1.MyViewHolder {
        val itemView =
            ItemCoinType1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = searchItemList[position]
        // val rate = rateListUSD.findRateFor(item.asset_id)
        holder.bind(item)
    }

    fun deleteItem(i: Int) {
        searchItemList.removeAt(i)
        notifyItemRemoved(i)
    }

    fun updateList(searchItemList: ArrayList<SearchItem>) {
        val tmpList = searchItemList.sortedWith(compareBy(nullsLast()) { it.rateCurrent })
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
            if (item.rateCurrent == null || item.timeUpdate == null || item.percents == null) {
                binding.tvValue.text = "Нет данных"
                binding.tvUpdateDate.text = "-"
                binding.tvPercents.text = "-"

                binding.clEnd.visibility = View.GONE
            } else {
                binding.clEnd.visibility = View.VISIBLE
                binding.tvValue.text = buildString {
                    append(String.format("%.2f", 1.0 / item.rateCurrent!!))
                    append(rateMarker)
                }
                binding.tvUpdateDate.text = buildString {
                    append(item.timeUpdate!!.substring(0, 10))
                    append("\n")
                    append(item.timeUpdate!!.substring(11, 19))
                }
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
