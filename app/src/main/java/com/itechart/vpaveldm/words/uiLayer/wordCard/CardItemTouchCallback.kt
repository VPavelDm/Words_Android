package com.itechart.vpaveldm.words.uiLayer.wordCard

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import kotlin.math.abs


abstract class CardItemTouchCallback(private val listener: CardItemTouchHelperAdapter) :
    ItemTouchHelper.Callback() {

    abstract fun getLeftDirectionDrawable(): ColorDrawable?
    abstract fun getRightDirectionDrawable(): ColorDrawable?
    abstract fun getLeftDirectionIcon(): Drawable?
    abstract fun getRightDirectionIcon(): Drawable?
    abstract fun canRightSwipe(): Boolean
    abstract fun canLeftSwipe(): Boolean

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        val swipeFlags =
            when {
                canRightSwipe() && canLeftSwipe() -> ItemTouchHelper.START or ItemTouchHelper.END
                canLeftSwipe() -> ItemTouchHelper.START
                canRightSwipe() -> ItemTouchHelper.END
                else -> 0
            }
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
            listener.onItemSwipedToLeft(holder.adapterPosition)
        else
            listener.onItemSwipedToRight(holder.adapterPosition)
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

        var progress = ((currentDistance / totalDistance) * 255).toInt()
        // Alpha = 0..255
        progress = if (progress > 255) 255 else progress
        if (dX < 0) {
            getLeftDirectionDrawable()?.apply {
                setBounds(view.right + dX.toInt() - view.width / 2, view.top, view.right, view.bottom)
                alpha = progress
                draw(c)
            } ?: return
            val deleteIconTop = view.top + (view.height - getLeftIconIntrinsicHeight()) / 2
            val deleteIconMargin = (view.height - getLeftIconIntrinsicHeight()) / 2
            val deleteIconLeft = view.right - deleteIconMargin - getLeftIconIntrinsicWidth()
            val deleteIconRight = view.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + getLeftIconIntrinsicWidth()
            getLeftDirectionIcon()?.apply {
                setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                alpha = progress
                draw(c)
            }
        } else {
            getRightDirectionDrawable()?.apply {
                setBounds(view.left, view.top, view.left + dX.toInt() + view.width / 2, view.bottom)
                alpha = progress
                draw(c)
            } ?: return
            val addIconTop = view.top + (view.height - getRightIconIntrinsicHeight()) / 2
            val addIconMargin = (view.height - getRightIconIntrinsicHeight()) / 2
            val addIconLeft = view.left + addIconMargin
            val addIconRight = view.left + addIconMargin + getRightIconIntrinsicWidth()
            val addIconBottom = addIconTop + getRightIconIntrinsicHeight()
            getRightDirectionIcon()?.apply {
                setBounds(addIconLeft, addIconTop, addIconRight, addIconBottom)
                alpha = progress
                draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float = 0f

    private fun getLeftIconIntrinsicHeight(): Int = getLeftDirectionIcon()?.intrinsicHeight ?: 0

    private fun getLeftIconIntrinsicWidth(): Int = getLeftDirectionIcon()?.intrinsicWidth ?: 0

    private fun getRightIconIntrinsicHeight(): Int = getRightDirectionIcon()?.intrinsicHeight ?: 0

    private fun getRightIconIntrinsicWidth(): Int = getRightDirectionIcon()?.intrinsicWidth ?: 0
}

interface CardItemTouchHelperAdapter {
    fun onItemSwipedToLeft(position: Int)
    fun onItemSwipedToRight(position: Int)
}