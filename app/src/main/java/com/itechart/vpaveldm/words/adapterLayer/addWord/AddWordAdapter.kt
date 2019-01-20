package com.itechart.vpaveldm.words.adapterLayer.addWord

import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.addWord.ViewHolderType.*
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.databinding.RecyclerItemAddExampleBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemAddWordBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemAddWordFooterBinding

class AddWordAdapter(private val viewModel: AddWordViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var examples: Array<Example> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM.ordinal -> {
                val binding = RecyclerItemAddWordBinding.inflate(inflater, parent, false)
                binding.handler = viewModel
                ItemViewHolder(binding)
            }
            EXAMPLE.ordinal -> {
                val binding = RecyclerItemAddExampleBinding.inflate(inflater, parent, false)
                val holder = ExampleViewHolder(binding)
                binding.viewHolder = holder
                holder
            }
            FOOTER.ordinal -> {
                val binding = RecyclerItemAddWordFooterBinding.inflate(inflater, parent, false)
                binding.handler = this
                FooterViewHolder(binding.root)
            }
            else -> {
                throw Error("Implement branch that equals $viewType")
            }
        }
    }

    override fun getItemCount(): Int = examples.size + 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExampleViewHolder -> {
                holder.bind(examples[position - 1])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM.ordinal
            itemCount - 1 -> FOOTER.ordinal
            else -> EXAMPLE.ordinal
        }
    }

    fun clickAddExample() {
        examples += Example()
        notifyItemInserted(itemCount - 1)
    }

    fun clickAddWord() {
        viewModel.addWord(examples = examples.toList())
    }

    class ItemViewHolder(binding: RecyclerItemAddWordBinding) : RecyclerView.ViewHolder(binding.root)

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ExampleViewHolder(binding: RecyclerItemAddExampleBinding) :
            RecyclerView.ViewHolder(binding.root) {
        val exampleObservable = ObservableField<Example>()

        fun bind(example: Example) {
            exampleObservable.set(example)
        }
    }

}

private enum class ViewHolderType {
    ITEM, EXAMPLE, FOOTER
}
