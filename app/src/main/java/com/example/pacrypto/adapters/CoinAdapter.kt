package com.example.pacrypto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.R
import com.example.pacrypto.data.SearchItem
import com.example.pacrypto.databinding.ItemCoinType1Binding
import com.example.pacrypto.util.Errors
import com.example.pacrypto.util.Rates

/**
 * RecyclerView adapter, that shows info in form of SearchItem
 *
 * Provides function for open full info fragment about chosen item
 * */
class CoinAdapter(
    val context: Context,
    val onItemClicked: (Int, SearchItem) -> Unit
) : RecyclerView.Adapter<CoinAdapter.MyViewHolder>() {

    private var searchItemList: ArrayList<SearchItem> = arrayListOf()
    private var rateMarker: String = Rates.USD_MARKER

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

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(searchItemList: ArrayList<SearchItem>) {
        val tmpList =
            if (rateMarker == Rates.USD_MARKER) searchItemList.sortedWith(compareBy(nullsFirst()) { it.rateCurrentUSD })
                .reversed()
            else searchItemList.sortedWith(compareBy(nullsFirst()) { it.rateCurrentRUB }).reversed()
        this.searchItemList = ArrayList(tmpList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
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

            // Basic info
            binding.apply {
                tvName.text = item.name
                tvTicker.text = item.ticker
                tvTicker.isSelected = true
            }

            // Rate value
            binding.apply {
                if (rateMarker == Rates.USD_MARKER) {
                    if (item.rateCurrentUSD == null) {
                        tvValue.text = Errors(context).NO_DATA
                    } else {
                        tvValue.text = buildString {
                            append(String.format("%.2f", item.rateCurrentUSD))
                            append(rateMarker)
                        }
                    }
                } else {
                    if (item.rateCurrentRUB == null) {
                        tvValue.text = Errors(context).NO_DATA
                    } else {
                        tvValue.text = buildString {
                            append(String.format("%.2f", item.rateCurrentRUB))
                            append(rateMarker)
                        }
                    }
                }
            }

            // Delta info & time
            binding.apply {
                if (item.timeUpdate == null || item.percents == null) {
                    tvUpdateDate.text = "-"
                    tvPercents.text = "-"
                    clEnd.visibility = View.GONE
                } else {
                    // time
                    tvUpdateDate.text = item.timeUpdate

                    // Percents color
                    if (item.percents!!.startsWith("+")) {
                        clEnd.background =
                            ContextCompat.getDrawable(context, R.color.green_300)
                    } else {
                        clEnd.background =
                            ContextCompat.getDrawable(context, R.color.orange_300)
                    }

                    // Percents value
                    if (item.percents!!.length < 9) tvPercents.text = item.percents
                    else tvPercents.text = "∞"

                    // Layout visibility
                    clEnd.visibility = View.VISIBLE
                }
            }

            // For spinning if value.length > view width
            binding.tvValue.isSelected = true

            // Whole card click listener
            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, item) }
        }
    }
}
