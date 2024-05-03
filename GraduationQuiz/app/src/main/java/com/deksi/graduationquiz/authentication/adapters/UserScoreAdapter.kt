package com.deksi.graduationquiz.authentication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.model.UserScore

class UserScoreAdapter(private val userScores: List<UserScore>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_score_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_score, parent, false)
            UserScoreViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserScoreAdapter.UserScoreViewHolder) {
            val userScore = userScores[position - 1]
            holder.bind(userScore, position)
        }
    }

    override fun getItemCount(): Int {
        // Add 1 for the header row
        return userScores.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    inner class UserScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val positionTextView: TextView = itemView.findViewById(R.id.positionTextView)
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val pointsTextView: TextView = itemView.findViewById(R.id.pointsTextView)

        fun bind(userScore: UserScore, position: Int) {
            positionTextView.text = position.toString()
            usernameTextView.text = userScore.username
            pointsTextView.text = userScore.totalPoints.toString()
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // No need to bind anything for the header
    }
}