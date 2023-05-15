package com.example.pacrypto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.R
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvsItem
import com.example.pacrypto.databinding.ItemInfoBinding


class InfoAdapter(
    val context: Context,
) : RecyclerView.Adapter<InfoAdapter.MyViewHolder>() {

    private var params = arrayListOf(
        "Цена на старте",
        "Самая высокая цена",
        "Самая низкая цена",
        "Цена на закрытии",
        "В общем\nопераций на",
        "Всего сделок"
    )
    private var list: ArrayList<Double> = arrayListOf()
    private var rateMarker: String = "$"

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