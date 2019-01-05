package com.itechart.vpaveldm.words.uiLayer.chooseTranslate

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import com.itechart.vpaveldm.words.R
import com.itechart.vpaveldm.words.adapterLayer.chooseTranslate.TranslateAdapter
import kotlinx.android.synthetic.main.fragment_choose_translate.view.*

class ChooseTranslateFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val translates = arguments?.getStringArrayList(TRANSLATES_KEY)
                ?: return super.onCreateDialog(savedInstanceState)
        context?.let { context ->
            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.fragment_choose_translate, null)
            view.translatesRV.apply {
                adapter = TranslateAdapter(translates)
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
            }
            return builder
                    .setTitle(context.getString(R.string.title_choose_translate))
                    .setView(view)
                    .setNegativeButton(context.getText(R.string.title_cancel), null)
                    .create()
        } ?: return super.onCreateDialog(savedInstanceState)
    }

    companion object {

        private const val TRANSLATES_KEY = "translates"

        fun create(translates: List<String>): ChooseTranslateFragment {
            val fragment = ChooseTranslateFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(TRANSLATES_KEY, ArrayList(translates))
            fragment.arguments = bundle
            return fragment
        }
    }

}