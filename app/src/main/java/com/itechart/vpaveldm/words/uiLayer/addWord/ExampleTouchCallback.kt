package com.itechart.vpaveldm.words.uiLayer.addWord

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.itechart.vpaveldm.words.adapterLayer.addWord.IExampleItemTouchHelperAdapter

class ExampleTouchCallback(private val listener: IExampleItemTouchHelperAdapter) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder): Int {
        return if (holder.adapterPosition == 0 || holder.adapterPosition == recyclerView.adapter?.itemCount?.minus(1)) {
            ItemTouchHelper.Callback.makeMovementFlags(0, 0)
        } else {
            ItemTouchHelper.Callback.makeMovementFlags(0, ItemTouchHelper.START)
        }
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwiped(holder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7F
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

}