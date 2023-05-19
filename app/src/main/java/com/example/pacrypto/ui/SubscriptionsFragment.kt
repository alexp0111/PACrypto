package com.example.pacrypto.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.pacrypto.R
import com.example.pacrypto.adapters.SubscriptionAdapter
import com.example.pacrypto.data.worker.SubItem
import com.example.pacrypto.data.worker.SubWorker
import com.example.pacrypto.data.worker.setUpSubscription
import com.example.pacrypto.databinding.FragmentSubscriptionsBinding
import com.example.pacrypto.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SUBSCRIPTIONS_FRAGMENT"

@AndroidEntryPoint
class SubscriptionsFragment : Fragment(R.layout.fragment_subscriptions),
    TimePickerDialog.OnTimeSetListener {

    private var fragmentSubscriptionsBinding: FragmentSubscriptionsBinding? = null

    var currentPos = -1
    var currentItem = SubItem(ticker = "", time = "", uuid = UUID.randomUUID())

    val subAdapter by lazy {
        SubscriptionAdapter(
            requireContext(),
            onDeleteClicked = { pos, item ->
                val uuid = removeSubItemFromSP(requireActivity(), item.ticker)
                val workManager = WorkManager.getInstance(requireContext())
                workManager.cancelWorkById(uuid)
                update()
            },
            onTimeClicked = { pos, item ->
                currentPos = pos
                currentItem = item

                TimePickerDialog(
                    requireContext(),
                    this,
                    item.time.substring(0, 2).toInt(),
                    item.time.substring(3, 5).toInt(),
                    true
                ).show()
            },
            onWeekClicked = { pos, item, weekpos, isPicked ->
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, item.time.substring(0, 2).toInt())
                calendar.set(Calendar.MINUTE, item.time.substring(3, 5).toInt())
                val weekDays = item.weekDay as MutableList<Int>
                if (weekpos.toCalendarConstant() in item.weekDay) {
                    weekDays.remove(weekpos.toCalendarConstant())
                } else {
                    weekDays.add(weekpos.toCalendarConstant())
                }
                val uuid = setUpSubscription(calendar, item, requireContext(), weekDays)
                removeSubItemFromSP(requireActivity(), item.ticker)
                addSubItemToSP(
                    requireActivity(), SubItem(
                        item.ticker,
                        item.time,
                        weekDays,
                        uuid
                    )
                )
                update()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSubscriptionsBinding.bind(view)
        fragmentSubscriptionsBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rv.layoutManager = manager
        binding.rv.adapter = subAdapter

        update()
    }

    private fun update() {
        val list = getAllSubItemsInSP(requireActivity())
        subAdapter.updateList(list)
    }

    override fun onDestroy() {
        fragmentSubscriptionsBinding = null
        super.onDestroy()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, p1)
        calendar.set(Calendar.MINUTE, p2)
        val time = calendar.time
        val uuid = setUpSubscription(calendar, currentItem, requireContext(), currentItem.weekDay)

        addSubItemToSP(
            requireActivity(),
            SubItem(
                ticker = currentItem.ticker,
                time = SimpleDateFormat("HH:mm").format(time),
                currentItem.weekDay,
                uuid = uuid
            )
        )

        update()
    }
}
