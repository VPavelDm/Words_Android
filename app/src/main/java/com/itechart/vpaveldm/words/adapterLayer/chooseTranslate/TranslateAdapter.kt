package com.itechart.vpaveldm.words.adapterLayer.chooseTranslate

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import kotlinx.android.synthetic.main.recycler_item_translate.view.*

class TranslateAdapter(private val translates: List<String>, private val listener: ITranslateClick)
    : RecyclerView.Adapter<TranslateAdapter.TranslateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TranslateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_translate, parent, false)
        return TranslateViewHolder(view)
    }

    override fun getItemCount(): Int = translates.size

    override fun onBindViewHolder(holder: TranslateViewHolder, position: Int) {
        holder.itemView.translateTV.text = translates[position]
    }

    inner class TranslateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                listener.translateClicked(itemView.translateTV.text.toString())
            }
        }
    }
}

interface ITranslateClick {
    fun translateClicked(translate: String)
}