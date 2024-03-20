package com.deksi.graduationquiz.slagalica.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivityMojBrojBinding
import com.deksi.graduationquiz.home.HomeActivity
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import java.util.LinkedList
import java.util.Random

class MojBroj : AppCompatActivity() {

    private lateinit var binding: ActivityMojBrojBinding
    private var input: MutableList<Button?> = LinkedList()
    private var generated = false
    private var currentNumber = 0
    private var timeLeft: CountDownTimer? = null
    private var generateTimer: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var totalScore: Int = 0
    private var currentRound = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMojBrojBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalScore = savedInstanceState?.getInt("totalScore") ?: 0
        savedInstanceState?.let {
            currentRound++
        }

        init()
        setUpFinalResultButtons()
        setUpActionBar()
    }

    private fun init() {
        val firstSingleDigit = binding.buttonFirstSingleDigit
        val secondSingleDigit = binding.buttonSecondSingleDigit
        val thirdSingleDigit = binding.buttonThirdSingleDigit
        val fourthSingleDigit = binding.buttonFourthSingleDigit
        val firstMediumDigit = binding.buttonFirstMediumDigit
        val secondMediumDigit = binding.buttonSecondMediumDigit
        val minus = binding.buttonMinus
        val plus = binding.buttonPlus
        val multiply = binding.buttonMultiply
        val divide = binding.buttonDivide
        val openBracket = binding.buttonOpenBrackets
        val closedBracket = binding.buttonCloseBrackets
        val delete = binding.buttonDelete
        firstSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.isEnabled = false
        }
        secondSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        thirdSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        fourthSingleDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        firstMediumDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        secondMediumDigit.setOnClickListener { v: View ->
            input.add(v as Button)
            displayInput()
            v.setEnabled(false)
        }
        plus.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        minus.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        multiply.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        divide.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        openBracket.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        closedBracket.setOnClickListener { v: View? ->
            input.add(v as Button?)
            displayInput()
        }
        delete.setOnClickListener { v: View? ->
            if (input.size == 0) {
            } else {
                input[input.size - 1]?.isEnabled = true
                input.removeAt(input.size - 1)
                displayInput()
            }
        }
    }

    private fun enableButtons() {
        val firstSingleDigit = binding.buttonFirstSingleDigit
        val secondSingleDigit = binding.buttonSecondSingleDigit
        val thirdSingleDigit = binding.buttonThirdSingleDigit
        val fourthSingleDigit = binding.buttonFourthSingleDigit
        val firstMediumDigit = binding.buttonFirstMediumDigit
        val secondMediumDigit = binding.buttonSecondMediumDigit
        val minus = binding.buttonMinus
        val plus = binding.buttonPlus
        val multiply = binding.buttonMultiply
        val divide = binding.buttonDivide
        val openBracket = binding.buttonOpenBrackets
        val closedBracket = binding.buttonCloseBrackets
        val delete = binding.buttonDelete
        firstSingleDigit.isEnabled = true
        secondSingleDigit.isEnabled = true
        thirdSingleDigit.isEnabled = true
        fourthSingleDigit.isEnabled = true
        firstMediumDigit.isEnabled = true
        secondMediumDigit.isEnabled = true
        minus.isEnabled = true
        plus.isEnabled = true
        multiply.isEnabled = true
        divide.isEnabled = true
        openBracket.isEnabled = true
        closedBracket.isEnabled = true
        delete.isEnabled = true
    }

    private fun evalSolution(solution: String?): Double? {
        val rhino: Context = Context.enter()
        rhino.optimizationLevel = -1
        return try {
            val scope: Scriptable = rhino.initStandardObjects()
            val result: Any = rhino.evaluateString(scope, solution, "JavaScript", 1, null)
            Context.toString(result).toDouble()
        } finally {
            Context.exit()
        }
    }

    private fun generateNumbers() {
        if (generated) return
        val singleDigits = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        val firstMediumDigits = arrayOf("10", "15", "20")
        val secondMediumDigits = arrayOf("25", "50", "75", "100")
        var idx = 0
        var generatedNumber: String? = null
        when (currentNumber) {
            0 -> {
                generatedNumber = "" + (Random().nextInt(998) + 2)
                setNumber(generatedNumber, currentNumber)
            }

            in 1..4 -> {
                idx = Random().nextInt(singleDigits.size)
                generatedNumber = singleDigits[idx]
                setNumber(generatedNumber, currentNumber)
            }

            5 -> {
                idx = Random().nextInt(firstMediumDigits.size)
                generatedNumber = firstMediumDigits[idx]
                setNumber(generatedNumber, currentNumber)
            }

            6 -> {
                idx = Random().nextInt(secondMediumDigits.size)
                generatedNumber = secondMediumDigits[idx]
                setNumber(generatedNumber, currentNumber)
            }
        }

        currentNumber++
    }

    private fun setNumber(number: String?, idx: Int) {
        val firstSingleDigit = binding.buttonFirstSingleDigit
        val secondSingleDigit = binding.buttonSecondSingleDigit
        val thirdSingleDigit = binding.buttonThirdSingleDigit
        val fourthSingleDigit = binding.buttonFourthSingleDigit
        val firstMediumDigit = binding.buttonFirstMediumDigit
        val secondMediumDigit = binding.buttonSecondMediumDigit
        val finalResult = binding.buttonFinalResult
        when (idx) {
            0 -> {
                finalResult.text = number
            }

            1 -> {
                firstSingleDigit.text = number
            }

            2 -> {
                secondSingleDigit.text = number
            }

            3 -> {
                thirdSingleDigit.text = number
            }

            4 -> {
                fourthSingleDigit.text = number
            }

            5 -> {
                firstMediumDigit.text = number
            }

            6 -> {
                initTimer()
                enableButtons()
                generated = true
                secondMediumDigit.text = number
            }
        }
    }

    private fun setUpFinalResultButtons() {
        val result = binding.buttonFinalResult
        val stopBtn = binding.buttonStopDigits

        binding.buttonDone.setOnClickListener {
            stopTimer()
            val solution = binding.textViewUserInput
            val guess = evalSolution(solution.text.toString())
            val actual = result.text.toString().toDouble()

            if (currentRound < 3) {
                if (actual == guess) {
                    totalScore = 20
                    showProgressDialogOnNextRound()
                    moveToResultsActivityWithDelay()
                } else {
                    totalScore = 5
                    showProgressDialogOnNextRound()
                    moveToResultsActivityWithDelay()
                }

            } else if (currentRound == 3) {
                if (actual == guess) {
                    totalScore = 20
                    showProgressDialogOnGameFinish()
                    moveToResultsActivityWithDelay()
                } else {
                    totalScore = 5
                    showProgressDialogOnGameFinish()
                    moveToResultsActivityWithDelay()
                }

            }

        }

        stopBtn.setOnClickListener {
            generateNumbers()
            generateTimer?.cancel()
            generateTimer?.start()
        }
    }

    private fun displayInput() {
        binding.buttonDone.isEnabled = true
        val buffer = StringBuffer()
        for (value in input) {
            buffer.append(value?.text.toString())
        }
        val inputText = binding.textViewUserInput
        inputText.text = buffer.toString()
    }

    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                totalScore = 0
                // treba dodati da kad istekne vreme da prikaze zavrsnu kombinaciju
                showProgressDialogOnTimeout()
                moveToResultsActivityWithDelay()
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

        val messageResourceId = resources.getIdentifier("mojbroj_message_time_up", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.mojbroj_message_time_up)
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

        val messageResourceId = resources.getIdentifier("mojbroj_message_on_finish", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.mojbroj_message_on_finish)
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

        val messageResourceId = resources.getIdentifier("mojbroj_message_on_round_starting", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId, currentRound)
        } else {
            getString(R.string.mojbroj_message_on_round_starting)
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

    private fun moveToResultsActivityWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            if (currentRound < 3) {
                saveTotalScoreToLocalPreferences()
                onSaveInstanceState(Bundle())
                recreate()
            } else {
                saveTotalScoreToLocalPreferences()
                val intent = Intent(this@MojBroj, Results::class.java)
                startActivity(intent)
                finish()
            }

        }, delayMilis)
    }

    private fun saveTotalScoreToLocalPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", android.content.Context.MODE_PRIVATE)
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
        val sharedPreferences = getSharedPreferences("GameScores", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the totalScore key from SharedPreferences
        editor.remove("totalScore")
        editor.apply()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", android.content.Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title)

        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Moj broj"
        titleTextView?.gravity = Gravity.CENTER
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
        MediaPlayerManager.release()
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
                MediaPlayerManager.release()
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