package com.itechart.vpaveldm.words.uiLayer.profile

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.uiLayer.wordCard.CardItemTouchCallback
import com.itechart.vpaveldm.words.uiLayer.wordCard.CardItemTouchHelperAdapter

class WordItemTouchCallback(context: Context, listener: CardItemTouchHelperAdapter): CardItemTouchCallback(listener) {

    private val removeBackground = ColorDrawable(Color.parseColor("#d80229"))
    private val editBackground = ColorDrawable(Color.parseColor("#1ba6d8"))
    private val removeIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)!!
    private val editIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_edit_black_24dp)!!

    override fun getLeftDirectionDrawable(): ColorDrawable? = removeBackground

    override fun getRightDirectionDrawable(): ColorDrawable? = editBackground

    override fun getLeftDirectionIcon(): Drawable? = removeIcon

    override fun getRightDirectionIcon(): Drawable? = editIcon

    override fun canRightSwipe(): Boolean = true

    override fun canLeftSwipe(): Boolean = true
}