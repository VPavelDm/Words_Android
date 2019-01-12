package com.itechart.vpaveldm.words.uiLayer.search

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.graphics.drawable.Drawable


class ItemDivider internal constructor(context: Context) : RecyclerView.ItemDecoration() {
    private val divider: Drawable

    init {
        val attrs = intArrayOf(android.R.attr.listDivider)
        val obtainStyledAttributes = context.obtainStyledAttributes(attrs)
        divider = obtainStyledAttributes.getDrawable(0)!!
        obtainStyledAttributes.recycle()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val left = parent.paddingLeft + 30
        val right = parent.width - parent.paddingRight - 30
        for (i in 0 until parent.childCount - 1) {
            val item = parent.getChildAt(i)

            val top = item.bottom + (item.layoutParams as RecyclerView.LayoutParams).bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}