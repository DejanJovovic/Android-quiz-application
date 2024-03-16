package com.deksi.graduationquiz.slagalica.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivitySkockoBinding
import java.util.Random

class Skocko : AppCompatActivity() {

    private lateinit var binding: ActivitySkockoBinding
    private var row = 0
    private var column = 0
    private var timeLeft: CountDownTimer? = null
    private var solution: IntArray = intArrayOf()
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var totalScore = 0
    private var currentRound = 2

    private var slots = Array(6) {
        arrayOfNulls<ImageView>(
            4
        )
    }
    private var guesses = Array(6) {
        IntArray(
            4
        )
    }

    private val resultViews = Array(8) { arrayOfNulls<ImageView>(4) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkockoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalScore = savedInstanceState?.getInt("totalScore") ?: 0
        savedInstanceState?.let {
            currentRound++
        }

        initResult()
        initSlots()
        initTimer()
        initButtons()
        generateSolution()
        setUpActionBar()
    }

    private fun initSlots() {
        slots[0][0] = binding.slot00
        slots[0][1] = binding.slot01
        slots[0][2] = binding.slot02
        slots[0][3] = binding.slot03
        slots[1][0] = binding.slot10
        slots[1][1] = binding.slot11
        slots[1][2] = binding.slot12
        slots[1][3] = binding.slot13
        slots[2][0] = binding.slot20
        slots[2][1] = binding.slot21
        slots[2][2] = binding.slot22
        slots[2][3] = binding.slot23
        slots[3][0] = binding.slot30
        slots[3][1] = binding.slot31
        slots[3][2] = binding.slot32
        slots[3][3] = binding.slot33
        slots[4][0] = binding.slot40
        slots[4][1] = binding.slot41
        slots[4][2] = binding.slot42
        slots[4][3] = binding.slot43
        slots[5][0] = binding.slot50
        slots[5][1] = binding.slot51
        slots[5][2] = binding.slot52
        slots[5][3] = binding.slot53
        for (i in 0..5) {
            for (j in 0..3) {
                guesses[i][j] = -1
            }
        }
    }


    private fun initButtons() {
        val tref = binding.imageViewTref
        val herc = binding.imageViewHerc
        val karo = binding.imageViewKaro
        val pik = binding.imageViewPik
        val cd = binding.imageViewCd
        val zvezda = binding.imageViewZvezda
        val delete = binding.imageViewDelete
        tref.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                0
            )
        }
        herc.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                1
            )
        }
        karo.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                2
            )
        }
        pik.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                3
            )
        }
        cd.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                4
            )
        }
        zvezda.setOnClickListener { view: View ->
            handleButtonClick(
                (view as ImageButton).drawable,
                5
            )
        }
        delete.setOnClickListener { handleDelete() }
    }

    private fun handleButtonClick(d: Drawable?, id: Int) {
        if (row >= slots.size) {
            return
        }
        slots[row][column]?.setImageDrawable(d)
        guesses[row][column] = id
        column++
        if (column >= 4) {
            column = 0
            checkMatch()
            row++
        }
        if (row == slots.size) {
            if (currentRound < 3) {
                showResult()
                stopTimer()
                showProgressDialogOnNextRound()
                moveToTheNextActivityWithDelay()
            } else {
                showResult()
                stopTimer()
                showProgressDialogOnGameFinish()
                moveToTheNextActivityWithDelay()
            }
        }
    }

    private fun generateSolution() {
        solution = IntArray(4)
        val generator = Random()
        for (i in 0..3) {
            val number = generator.nextInt(6)
            solution[i] = number
        }
    }

    private fun initResult() {
        resultViews[0] =
            arrayOf(binding.result01, binding.results02, binding.results03, binding.result04)
        resultViews[1] =
            arrayOf(binding.result11, binding.results12, binding.results13, binding.result14)
        resultViews[2] =
            arrayOf(binding.result21, binding.results22, binding.results23, binding.result24)
        resultViews[3] =
            arrayOf(binding.result31, binding.results32, binding.results33, binding.result34)
        resultViews[4] =
            arrayOf(binding.result41, binding.results42, binding.results43, binding.result44)
        resultViews[5] =
            arrayOf(binding.result51, binding.results52, binding.results53, binding.result54)
        resultViews[6] =
            arrayOf(binding.result61, binding.results62, binding.results63, binding.result64)
        resultViews[7] =
            arrayOf(binding.result71, binding.results72, binding.results73, binding.result74)
    }

    // Helper function to set background color based on correct and misplaced signs
    private fun setSignColor(imageView: ImageView, count: Int) {
        when {
            count > 0 -> imageView.setBackgroundColor(Color.RED)
            count == 0 -> imageView.setBackgroundColor(Color.YELLOW)
            else -> imageView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun checkMatch() {
        var correct = 0
        var misplaced = 0
        val confirmed = booleanArrayOf(false, false, false, false)
        for (i in 0..3) {
            if (guesses[row][i] == solution[i]) {
                correct++
                confirmed[i] = true
            }
        }
        for (i in 0..3) {
            if (confirmed[i]) {
                continue
            }
            for (j in 0..3) {
                if (confirmed[j]) {
                    continue
                }
                if (guesses[row][i] == solution[j]) {
                    misplaced++
                    confirmed[j] = true
                    break
                }
            }
        }
        for (i in 0 until 4) {
            val imageView = resultViews[row][i]!!

            if (i < correct) {
                // Set color for correct slots
                setSignColor(imageView, 1)  // 1 represents correct
            } else if (i < correct + misplaced) {
                // Set color for misplaced slots
                setSignColor(imageView, 0)  // 0 represents misplaced
            } else {
                // Set color for the remaining slots (if any)
                setSignColor(imageView, -1)
            }
        }

        if (correct == 4) {
            if (currentRound < 3) {
                showResult()
                showScore()
                stopTimer()
                showProgressDialogOnNextRound()
                moveToTheNextActivityWithDelay()
            } else {
                showResult()
                showScore()
                stopTimer()
                showProgressDialogOnGameFinish()
                moveToTheNextActivityWithDelay()
            }

        }
    }

    private fun showScore() {
        if (row / 2 == 0) {
            totalScore = 20
        } else if (row / 2 == 1) {
            totalScore = 15
        } else if (row / 2 == 2) {
            totalScore = 10
        }
    }

    private fun showResult() {
        val slots = arrayOfNulls<ImageView>(4)
        slots[0] = binding.slot70
        slots[1] = binding.slot71
        slots[2] = binding.slot72
        slots[3] = binding.slot73
        for (i in 0..3) {
            when (solution[i]) {
                0 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.tref))
                1 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.herc))
                2 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.karo))
                3 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.pik))
                4 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.cd))
                5 -> slots[i]?.setImageDrawable(getDrawable(R.drawable.zvezda))
            }
        }

    }

    private fun handleDelete() {
        if (column == 0) {
            return
        }
        slots[row][column - 1]!!.setImageDrawable(getDrawable(R.drawable.frame))
        guesses[row][column - 1] = -1
        if (column > 0) {
            column--
        }
    }

    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                showResult()
                showProgressDialogOnTimeout()
                moveToTheNextActivityWithDelay()
            }
        }
        (timeLeft as CountDownTimer).start()
    }

    private fun startTimerProgressDialog() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (totalTime - millisUntilFinished).toInt()

                val messageResourceId = resources.getIdentifier("timer_score", "string", packageName)
                val messageScore = if (messageResourceId != 0) {
                    getString(messageResourceId)
                } else {
                    getString(R.string.timer_score)
                }

                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining     $messageScore: $totalScore"
                progressDialog?.setMessage(message)

            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun stopTimer() {
        timeLeft?.cancel()
    }

    private fun showProgressDialogOnTimeout() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("skocko_message_time_up", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.skocko_message_time_up)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }


    private fun showProgressDialogOnGameFinish() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("skocko_message_on_finish", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.skocko_message_on_finish)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogOnNextRound() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("skocko_message_on_round_starting", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId, currentRound)
        } else {
            getString(R.string.skocko_message_on_round_starting)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun moveToTheNextActivityWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            if (currentRound < 3) {
                saveTotalScoreToLocalPreferences()
                onSaveInstanceState(Bundle())
                row = 0
                recreate()
            } else {
                saveTotalScoreToLocalPreferences()
                val intent = Intent(this@Skocko, KorakPoKorak::class.java)
                startActivity(intent)
                finish()
            }

        }, delayMilis)
    }

    private fun saveTotalScoreToLocalPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve the existing total score
        val currentTotalScore = sharedPreferences.getInt("totalScore", 0)

        // Add the local total score to the overall total score
        val newTotalScore = currentTotalScore + totalScore

        // Save the updated total score
        editor.putInt("totalScore", newTotalScore)
        editor.apply()
    }

    private fun clearTotalScoreFromPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the totalScore key from SharedPreferences
        editor.remove("totalScore")
        editor.apply()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title)

        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Skocko"
        titleTextView?.gravity = Gravity.CENTER
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the state
        outState.putInt("currentRound", currentRound)
        outState.putInt("totalScore", totalScore)
    }
    override fun onResume() {
        super.onResume()
        MediaPlayerManager.start()
    }

}