package com.itechart.vpaveldm.words.adapterLayer.studyWord

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.itechart.vpaveldm.words.dataLayer.word.Example
import com.itechart.vpaveldm.words.dataLayer.word.Word
import com.itechart.vpaveldm.words.databinding.RecyclerItemStudyWordExampleBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemStudyWordQuestionBinding
import com.itechart.vpaveldm.words.databinding.RecyclerItemStudyWordTranslateBinding

class StudyWordAdapter(private val viewModel: StudyWordViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var word: Word = Word()
    private var text: Int = 0
    private var translate: Int = 1
    private var example: Int = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            text -> {
                val binding = RecyclerItemStudyWordQuestionBinding.inflate(inflater, parent, false)
                val holder = StudyTextViewHolder(binding)
                binding.handler = holder
                holder
            }
            translate -> {
                val binding = RecyclerItemStudyWordTranslateBinding.inflate(inflater, parent, false)
                val holder = StudyTranslateViewHolder(binding)
                binding.handler = holder
                holder
            }
            else -> {
                val binding = RecyclerItemStudyWordExampleBinding.inflate(inflater, parent, false)
                val holder = StudyExampleViewHolder(binding)
                binding.handler = holder
                holder
            }
        }
    }

    override fun getItemCount(): Int = if (viewModel.translateVisible.get()) word.examples.size + 2 else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StudyTextViewHolder -> holder.bind(word)
            is StudyTranslateViewHolder -> holder.bind(word)
            is StudyExampleViewHolder -> holder.bind(word.examples[position - 2])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && word.count % 2 != 0 -> translate
            position == 0 -> text
            position == 1 && viewModel.translateVisible.get() && word.count % 2 != 0 -> text
            position == 1 && viewModel.translateVisible.get() -> translate
            else -> example
        }
    }


    fun swapWord(word: Word) {
        this.word = word
        text = word.count % 2
        translate = 1 - word.count % 2
        notifyDataSetChanged()
    }

    fun showAnswer() {
        notifyItemRangeInserted(1, word.examples.size + 1)
    }

    class StudyTextViewHolder(val binding: RecyclerItemStudyWordQuestionBinding) : RecyclerView.ViewHolder(binding.root) {

        val wordObservable = ObservableField<Word>()

        fun bind(word: Word) {
            wordObservable.set(word)
        }
    }

    class StudyTranslateViewHolder(val binding: RecyclerItemStudyWordTranslateBinding) : RecyclerView.ViewHolder(binding.root) {
        val translateObservable = ObservableField<String>()

        fun bind(word: Word) {
            translateObservable.set(word.translate)
        }
    }

    class StudyExampleViewHolder(val binding: RecyclerItemStudyWordExampleBinding) : RecyclerView.ViewHolder(binding.root) {
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

private enum class StudyViewHolderType(val value: Int) {
    TEXT(0), TRANSLATE(1), EXAMPLE(2)
}