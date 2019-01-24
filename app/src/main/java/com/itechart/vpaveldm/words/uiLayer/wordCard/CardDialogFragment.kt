package com.itechart.vpaveldm.words.uiLayer.wordCard

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.profile.WordAdapter
import com.itechart.vpaveldm.words.dataLayer.word.Word
import kotlinx.android.synthetic.main.fragment_item_word.view.*


class CardDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_word, container, false)
        (arguments?.getSerializable(key) as? Word)?.let { word ->
            initWord(view, word)
        }
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    private fun initWord(view: View, word: Word) {
        view.wordRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = WordAdapter(word)
            addItemDecoration(CardItemDivider(activity!!))
        }
        view.closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {

        private const val key = "word_argument"

        fun create(word: Word): CardDialogFragment {
            val bundle = Bundle()
            bundle.putSerializable(key, word)
            val fragment = CardDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}