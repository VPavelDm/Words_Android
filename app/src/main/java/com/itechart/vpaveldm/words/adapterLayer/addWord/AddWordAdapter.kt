package com.itechart.vpaveldm.words.adapterLayer.addWord

import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.itechart.vpaveldm.words.adapterLayer.addWord.ViewHolderType.*
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.databinding.RecyclerItemAddExampleBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemAddWordBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemAddWordFooterBinding

class AddWordAdapter(private val viewModel: AddWordViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    IExampleItemTouchHelperAdapter {

    private var examples = arrayListOf<Example>()
    private var createdExampleCount = 0

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
                holder.bind(examples[position - 1], createdExampleCount)
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

    override fun onItemSwiped(position: Int) {
        examples.removeAt(position - 1)
        notifyItemRemoved(position)
    }

    fun clickAddExample() {
        createdExampleCount += 1
        examples.add(Example())
        notifyItemInserted(itemCount - 1)
    }

    fun clickAddWord() {
        viewModel.addWord(examples)
    }

    class ItemViewHolder(binding: RecyclerItemAddWordBinding) : RecyclerView.ViewHolder(binding.root)

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ExampleViewHolder(binding: RecyclerItemAddExampleBinding) : RecyclerView.ViewHolder(binding.root) {
        val exampleObservable = ObservableField<Example>()
        val positionObservable = ObservableInt(1)

        fun bind(example: Example, position: Int) {
            exampleObservable.set(example)
            positionObservable.set(position)
        }
    }

}

private enum class ViewHolderType {
    ITEM, EXAMPLE, FOOTER
}

interface IExampleItemTouchHelperAdapter {
    fun onItemSwiped(position: Int)
}