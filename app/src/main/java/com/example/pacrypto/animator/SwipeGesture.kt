package com.example.pacrypto.animator

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

/**
 * Class that allows recyclerView to handle horizontal swipes on items for deletion
 * */
abstract class SwipeGesture(val context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val deleteColor = ContextCompat.getColor(context, R.color.background_main)
    private val deleteIcon = R.drawable.ic_delete_decorator

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c, recyclerView, viewHolder, dX, dY,
            actionState, isCurrentlyActive
        )
            .addBackgroundColor(deleteColor)
            .addActionIcon(deleteIcon)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}