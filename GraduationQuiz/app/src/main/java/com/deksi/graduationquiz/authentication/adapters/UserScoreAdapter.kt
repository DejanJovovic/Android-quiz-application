package com.deksi.graduationquiz.authentication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.model.UserScore

class UserScoreAdapter(private val userScores: List<UserScore>) :
    RecyclerView.Adapter<UserScoreAdapter.UserScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_score, parent, false)
        return UserScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserScoreViewHolder, position: Int) {
        val userScore = userScores[position]
        holder.bind(userScore)
    }

    override fun getItemCount(): Int {
        return userScores.size
    }

    inner class UserScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val pointsTextView: TextView = itemView.findViewById(R.id.pointsTextView)

        fun bind(userScore: UserScore) {
            val username = userScore.username
            usernameTextView.text = userScore.username
            pointsTextView.text = userScore.totalPoints.toString()
        }
    }
}