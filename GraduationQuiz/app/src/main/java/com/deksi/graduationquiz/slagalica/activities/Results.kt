package com.deksi.graduationquiz.slagalica.activities

import android.app.AlertDialog
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

        val messageResourceId = resources.getIdentifier("timer_total_score", "string", packageName)
        val messageScore = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.timer_total_score)
        }

        // Now, 'totalScore' contains the sum of scores across all games
        binding.textViewTotalScore.text = "$messageScore: $totalScore"
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
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("results_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.results_message)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
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
                finish()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTotalScoreFromPreferences()
    }

    override fun onBackPressed() {
        val messageResourceId = resources.getIdentifier("on_back_pressed_game_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.on_back_pressed_game_message)
        }

        val yesResourceId = resources.getIdentifier("yes", "string", packageName)
        val yes = if (yesResourceId != 0) {
            getString(yesResourceId)
        } else {
            getString(R.string.yes)
        }

        val noResourceId = resources.getIdentifier("no", "string", packageName)
        val no = if (noResourceId != 0) {
            getString(noResourceId)
        } else {
            getString(R.string.no)
        }

        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(yes) { _, _ ->
                super.onBackPressed()
                clearTotalScoreFromPreferences()
            }
            .setNegativeButton(no) { _, _ ->
                // Do nothing
            }
            .show()

    }
}