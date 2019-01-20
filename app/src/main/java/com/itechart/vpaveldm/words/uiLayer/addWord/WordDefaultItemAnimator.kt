package com.itechart.vpaveldm.words.uiLayer.addWord

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_item_add_example.view.*

class WordDefaultItemAnimator : DefaultItemAnimator() {

    override fun onAddStarting(item: RecyclerView.ViewHolder?) {
        super.onAddStarting(item)
        item?.itemView?.enterTextET?.requestFocus()
    }

}