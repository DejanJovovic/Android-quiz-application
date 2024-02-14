package com.deksi.graduationquiz.slagalica.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityResultsBinding
import com.deksi.graduationquiz.home.HomeActivity

class Results : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private var progressDialog: ProgressDialog? = null
    private val totalTime: Long = 5000
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickListeners()
        showTotalScore()
    }

    private fun onClickListeners() {
        binding.buttonBackToHome.setOnClickListener {
            showProgressDialogOnNextRound()
            clearTotalScoreFromPreferences() // clears the current totalScore value from local memory
        }
    }

    private fun showTotalScore() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)

        // Retrieve the overall total score
        val totalScore = sharedPreferences.getInt("totalScore", 0)

        // Now, 'totalScore' contains the sum of scores across all games
        binding.textViewTotalScore.text = "Your total score is: $totalScore"
    }

    private fun clearTotalScoreFromPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the totalScore key from SharedPreferences
        editor.remove("totalScore")
        editor.apply()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnNextRound() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Thanks for playing Slagalica!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun startTimerProgressDialog() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (totalTime - millisUntilFinished).toInt()

                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining"
                progressDialog?.setMessage(message)

            }

            override fun onFinish() {
                dismissProgressDialog()
                val intent = Intent(this@Results, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTotalScoreFromPreferences()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearTotalScoreFromPreferences()
    }
}