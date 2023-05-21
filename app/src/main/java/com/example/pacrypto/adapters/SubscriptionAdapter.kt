package com.example.pacrypto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.R
import com.example.pacrypto.data.worker.SubItem
import com.example.pacrypto.databinding.ItemSubscriptionBinding
import com.example.pacrypto.util.toCalendarConstant

/**
 * RecyclerView adapter, that show info about user's actual subscriptions
 *
 * Provide functions for deleting sub, changing time of sub & set days of the week,
 *  when user want to be notified
 * */
class SubscriptionAdapter(
    val context: Context,
    val onDeleteClicked: (Int, SubItem) -> Unit,
    val onTimeClicked: (Int, SubItem) -> Unit,
    val onWeekClicked: (Int, SubItem, Int) -> Unit,
) : RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder>() {

    private var list: ArrayList<SubItem> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionAdapter.MyViewHolder {
        val itemView =
            ItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<SubItem>) {
        this.list = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubItem) {
            binding.apply {
                // viewing info
                tvTicker.text = item.ticker
                tvTime.text = item.time

                // clickers
                tvTime.setOnClickListener { onTimeClicked.invoke(absoluteAdapterPosition, item) }
                ivDelete.setOnClickListener {
                    onDeleteClicked.invoke(
                        absoluteAdapterPosition,
                        item
                    )
                }

                val weekPicker =
                    arrayListOf(tvWeek1, tvWeek2, tvWeek3, tvWeek4, tvWeek5, tvWeek6, tvWeek7)

                weekPicker.forEachIndexed { index, textView ->
                    if (index.toCalendarConstant() !in item.weekDay){
                        textView.setTextColor(ContextCompat.getColor(context, R.color.color_off))
                    } else {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.color_on))
                    }
                }
                weekPicker.forEachIndexed { index, textView ->
                    textView.setOnClickListener {
                        onWeekClicked.invoke(
                            absoluteAdapterPosition,
                            item,
                            index
                        )
                    }
                }
            }
        }
    }
}