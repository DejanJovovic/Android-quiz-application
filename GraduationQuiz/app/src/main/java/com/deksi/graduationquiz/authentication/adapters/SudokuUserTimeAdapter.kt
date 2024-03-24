package com.deksi.graduationquiz.authentication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.model.SudokuUserTime

class SudokuUserTimeAdapter(private val userTime: List<SudokuUserTime>) :
    RecyclerView.Adapter<SudokuUserTimeAdapter.SudokuUserTimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SudokuUserTimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sudoku_user_time, parent, false)
        return SudokuUserTimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SudokuUserTimeViewHolder, position: Int) {
        val sudokuUserTime = userTime[position]
        holder.bind(sudokuUserTime, position + 1)
    }

    override fun getItemCount(): Int {
        return userTime.size
    }

    inner class SudokuUserTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val positionTextView: TextView = itemView.findViewById(R.id.positionTextView)
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        fun bind(sudokuUserTime: SudokuUserTime, position: Int) {
            positionTextView.text = position.toString()
            usernameTextView.text = sudokuUserTime.username
            timeTextView.text = sudokuUserTime.totalTime.toString()
        }
    }
}