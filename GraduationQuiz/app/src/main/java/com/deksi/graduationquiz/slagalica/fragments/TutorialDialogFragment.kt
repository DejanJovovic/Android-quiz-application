package com.deksi.graduationquiz.slagalica.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.slagalica.adapters.TutorialPagesAdapter

class TutorialDialogFragment(private val textList: List<Pair<String, String>>, private val closeButtonClickListener: CloseButtonClickListener) : DialogFragment() {

    companion object {
        private const val ARG_TEXT_LIST = "text_list"

        fun newInstance(textList: List<Pair<String, String>>, listener: CloseButtonClickListener, username:String): TutorialDialogFragment {
            val fragment = TutorialDialogFragment(textList, listener)
            val args = Bundle()
            val titleList = textList.map { it.first }
            val contentList = textList.map { it.second }
            args.putStringArrayList("titleList", ArrayList(titleList))
            args.putStringArrayList("contentList", ArrayList(contentList))
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_tutorial_dialog, null)

        val viewPager: ViewPager = view.findViewById(R.id.viewPager)
        val closeButton: ImageView = view.findViewById(R.id.image_view_close_button)

        isCancelable = false

        val adapter = TutorialPagesAdapter(requireContext(), textList)
        val username = arguments?.getString("username")
        viewPager.adapter = adapter

        closeButton.setOnClickListener {
            closeButtonClickListener.onCloseButtonClicked()
            dismiss() // Dismiss dialog
        }

        builder.setView(view)
        return builder.create()
    }

    interface CloseButtonClickListener {
        fun onCloseButtonClicked()
    }
}