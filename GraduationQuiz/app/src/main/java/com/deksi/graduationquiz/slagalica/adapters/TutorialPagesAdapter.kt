package com.deksi.graduationquiz.slagalica.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.deksi.graduationquiz.R

class TutorialPagesAdapter(private val context: Context, private val textList: List<Pair<String, String>>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.page_item, container, false)
        val titleTextView: TextView = view.findViewById(R.id.text_view_title_tutorial)
        val tutorialTextView: TextView = view.findViewById(R.id.text_view_description_tutorial)

        val (title, tutorialText) = textList[position]

        titleTextView.text = title
        tutorialTextView.text = tutorialText

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return textList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}