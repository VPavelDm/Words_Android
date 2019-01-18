package com.itechart.vpaveldm.words.uiLayer.word

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.itechart.vpaveldm.words.adapterLayer.word.WordItemTouchHelperAdapter

class WordItemTouchCallback(private val listener: WordItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        sHolder: RecyclerView.ViewHolder,
        eHolder: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.START)
            listener.onItemSwipedToRemove(holder.adapterPosition)
        else
            listener.onItemSwipedToAdd(holder.adapterPosition)
    }
}