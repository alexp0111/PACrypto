package com.example.pacrypto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.databinding.ItemInfoBinding
import com.example.pacrypto.util.OhlcvsInfo
import com.example.pacrypto.util.Rates

/**
 * RecyclerView adapter, that shows info in form of OHLCV (Open, High, Low, Close, Volume)
 * Have 2 lists - first for constant text info & second - for actual values
 * */
class InfoAdapter(
    val context: Context,
) : RecyclerView.Adapter<InfoAdapter.MyViewHolder>() {

    private var params = OhlcvsInfo(context).parameters
    private var list: ArrayList<Double> = arrayListOf()
    private var rateMarker: String = Rates.USD_MARKER

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InfoAdapter.MyViewHolder {
        val itemView =
            ItemInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val param = params[position]
        val item = list[position]
        holder.bind(param, item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Double>, marker: String) {
        this.list = ArrayList(list)
        this.rateMarker = marker
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(param: String, item: Double) {
            binding.tvParam.text = param
            binding.tvValue.text = if (param != params.last()){
                String.format("%.2f", item) + rateMarker
            } else {
                item.toInt().toString()
            }
            binding.tvValue.isSelected = true
        }
    }
}