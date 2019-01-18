package com.itechart.vpaveldm.words.uiLayer.word

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.word.WordItemTouchHelperAdapter
import kotlin.math.abs


class WordItemTouchCallback(context: Context, private val listener: WordItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {

    private val removeBackground = ColorDrawable(Color.parseColor("#d80229"))
    private val addBackground = ColorDrawable(Color.parseColor("#FF92DC5B"))
    private val removeIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)!!
    private val addIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_check_black_24dp)!!
    private val removeIconIntrinsicHeight = removeIcon.intrinsicHeight
    private val removeIconIntrinsicWidth = removeIcon.intrinsicWidth
    private val addIconIntrinsicHeight = addIcon.intrinsicHeight
    private val addIconIntrinsicWidth = addIcon.intrinsicWidth

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return ItemTouchHelper.Callback.makeMovementFlags(0, swipeFlags)
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

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder.adapterPosition < 0) return

        val view = viewHolder.itemView

        // How much you need to remove view
        val totalDistance = recyclerView.width * getSwipeThreshold(viewHolder)
        // How much you did to remove view
        val currentDistance = abs(dX)

        Log.i("myAppTAG", "total = $totalDistance")
        Log.i("myAppTAG", "current = $currentDistance")
        Log.i("myAppTAG", "alpha = ${currentDistance / totalDistance}")


        var progress = ((currentDistance / totalDistance) * 255).toInt()
        // Alpha = 0..255
        progress = if (progress > 255) 255 else progress
        if (dX < 0) {
            removeBackground.apply {
                setBounds(view.right + dX.toInt() - view.width / 2, view.top, view.right, view.bottom)
                alpha = progress
                draw(c)
            }
            val deleteIconTop = view.top + (view.height - removeIconIntrinsicHeight) / 2
            val deleteIconMargin = (view.height - removeIconIntrinsicHeight) / 2
            val deleteIconLeft = view.right - deleteIconMargin - removeIconIntrinsicWidth
            val deleteIconRight = view.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + removeIconIntrinsicHeight
            removeIcon.apply {
                setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                alpha = progress
                draw(c)
            }
        } else {
            addBackground.apply {
                setBounds(view.left, view.top, view.left + dX.toInt() + view.width / 2, view.bottom)
                alpha = progress
                draw(c)
            }
            val addIconTop = view.top + (view.height - addIconIntrinsicHeight) / 2
            val addIconMargin = (view.height - addIconIntrinsicHeight) / 2
            val addIconLeft = view.left + addIconMargin
            val addIconRight = view.left + addIconMargin + addIconIntrinsicWidth
            val addIconBottom = addIconTop + addIconIntrinsicHeight
            addIcon.apply {
                setBounds(addIconLeft, addIconTop, addIconRight, addIconBottom)
                alpha = progress
                draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}