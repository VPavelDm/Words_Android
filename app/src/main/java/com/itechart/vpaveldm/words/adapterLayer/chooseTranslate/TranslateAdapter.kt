package com.itechart.vpaveldm.words.adapterLayer.chooseTranslate

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.R
import kotlinx.android.synthetic.main.recycler_item_translate.view.*

class TranslateAdapter(private val translates: List<String>) :
    RecyclerView.Adapter<TranslateAdapter.TranslateViewHolder>() {

    private val selectedItems = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): TranslateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_translate, parent, false)
        return TranslateViewHolder(view)
    }

    override fun getItemCount(): Int = translates.size

    override fun onBindViewHolder(holder: TranslateViewHolder, position: Int) {
        holder.itemView.translateTV.text = translates[position]
        holder.itemView.isSelected = selectedItems.get(position, false)
    }

    fun getChosenTranslates(): List<String> {
        val results = arrayListOf<String>()
        for (i in 0 until selectedItems.size()) {
            val index = selectedItems.keyAt(i)
            results.add(translates[index])
        }
        return results.toList()
    }

    inner class TranslateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                if (selectedItems.get(layoutPosition, false)) {
                    selectedItems.delete(layoutPosition)
                    itemView.isSelected = false
                } else {
                    selectedItems.put(layoutPosition, true)
                    itemView.isSelected = true
                }
            }
        }
    }
}