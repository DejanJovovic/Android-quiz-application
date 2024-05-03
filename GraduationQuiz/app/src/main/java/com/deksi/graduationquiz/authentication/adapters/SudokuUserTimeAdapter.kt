package com.deksi.graduationquiz.authentication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.model.SudokuUserTime

class SudokuUserTimeAdapter(private val userTime: List<SudokuUserTime>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sudoku_user_time_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sudoku_user_time, parent, false)
            SudokuUserTimeViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SudokuUserTimeViewHolder) {
            val sudokuUserTime = userTime[position - 1]
            holder.bind(sudokuUserTime, position)
        }
    }

    override fun getItemCount(): Int {
        // Add 1 for the header row
        return userTime.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    inner class SudokuUserTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val positionTextView: TextView = itemView.findViewById(R.id.positionTextView)
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        private val difficultyTextView: TextView = itemView.findViewById(R.id.difficultyTextView)

        fun bind(sudokuUserTime: SudokuUserTime, position: Int) {
            positionTextView.text = position.toString()
            usernameTextView.text = sudokuUserTime.username
            timeTextView.text = sudokuUserTime.totalTime.toString()
            difficultyTextView.text = sudokuUserTime.difficulty
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // No need to bind anything for the header
    }
}