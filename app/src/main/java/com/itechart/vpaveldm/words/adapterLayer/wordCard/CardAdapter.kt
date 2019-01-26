package com.itechart.vpaveldm.words.adapterLayer.wordCard

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.databinding.RecyclerItemCardWordExampleBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemCardWordQuestionBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemCardWordTranslateBinding

abstract class CardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var text: Int = 0
    private var translate: Int = 1
    private var example: Int = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            text -> {
                val binding = RecyclerItemCardWordQuestionBinding.inflate(inflater, parent, false)
                val holder = WordViewHolder(binding)
                binding.handler = holder
                holder
            }
            translate -> {
                val binding = RecyclerItemCardWordTranslateBinding.inflate(inflater, parent, false)
                val holder = TranslateViewHolder(binding)
                binding.handler = holder
                holder
            }
            else -> {
                val binding = RecyclerItemCardWordExampleBinding.inflate(inflater, parent, false)
                val holder = ExampleViewHolder(binding)
                binding.handler = holder
                holder
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WordViewHolder -> holder.bind(getWord(), isEnglishCard())
            is TranslateViewHolder -> holder.bind(getWord(), isEnglishCard())
            is ExampleViewHolder -> holder.bind(getWord().examples[position - 2])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && isEnglishCard() -> text
            position == 0 -> translate
            position == 1 && isFullCardVisible() && isEnglishCard() -> translate
            position == 1 && isFullCardVisible() -> text
            else -> example
        }
    }

    abstract override fun getItemCount(): Int

    abstract fun isEnglishCard(): Boolean

    abstract fun isFullCardVisible(): Boolean

    abstract fun getWord(): Word

    class WordViewHolder(val binding: RecyclerItemCardWordQuestionBinding) : RecyclerView.ViewHolder(binding.root) {

        val wordObservable = ObservableField<Word>()
        val englishCard = ObservableBoolean(false)

        fun bind(word: Word, isEnglishCard: Boolean) {
            wordObservable.set(word)
            englishCard.set(isEnglishCard)
        }
    }

    class TranslateViewHolder(val binding: RecyclerItemCardWordTranslateBinding) : RecyclerView.ViewHolder(binding.root) {
        val translateObservable = ObservableField<String>()
        val englishCard = ObservableBoolean(false)

        fun bind(word: Word, isEnglishCard: Boolean) {
            translateObservable.set(word.translate)
            englishCard.set(isEnglishCard)
        }
    }

    class ExampleViewHolder(val binding: RecyclerItemCardWordExampleBinding) : RecyclerView.ViewHolder(binding.root) {
        val exampleObservable = ObservableField<Example>()
        val translateVisible = ObservableBoolean(false)

        fun bind(example: Example) {
            exampleObservable.set(example)
        }

        fun click() {
            translateVisible.set(!translateVisible.get())
        }
    }

}