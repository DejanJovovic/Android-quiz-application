package com.deksi.graduationquiz.sudoku


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.authentication.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivitySudokuBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.slagalica.activities.Skocko

import com.deksi.graduationquiz.sudoku.game.Cell
import com.deksi.graduationquiz.sudoku.game.SudokuGame
import com.deksi.graduationquiz.sudoku.view.SudokuBoardView
import com.deksi.graduationquiz.sudoku.viewModel.SudokuViewModel


class Sudoku : AppCompatActivity(), SudokuBoardView.OnTouchListener, SudokuGame.SudokuGameListener {

    private lateinit var viewModel: SudokuViewModel
    private lateinit var binding: ActivitySudokuBinding
    private var timeLeft: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var score  = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTimer()
        setUpActionBar()
        getDifficultyAndUpdateCells()
        registerBoardViewListener()
        forEachIndexButtons()
        startBackgroundMusic()

    }

    private fun forEachIndexButtons() {
        val buttons = listOf(binding.buttonOne, binding.buttonTwo, binding.buttonThree, binding.buttonFour, binding.buttonFive, binding.buttonSix,
            binding.buttonSeven, binding.buttonEight, binding.buttonNine)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }
    }

    private fun registerBoardViewListener(){
        binding.sudokuBoardView.registerListener(this)
    }

    private fun getDifficultyAndUpdateCells() {
        val difficulty = intent.getStringExtra("difficulty") ?: "easy"

        viewModel = ViewModelProvider(this)[SudokuViewModel::class.java]
        viewModel.sudokuGame = SudokuGame(listener = this, difficulty = difficulty)
        // observing what's happening to the selecetedCellData
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
    }


    //runs only when the cells is not null
    private fun updateCells(cells: List<Cell>?) = cells?.let {
        binding.sudokuBoardView.updateCells(cells)
    }

    //this will only run if the pair is not null
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        binding.sudokuBoardView.updatedSelectedCellUI(cell.first, cell.second)
    }

    override fun onCellTouched(row: Int, col: Int){
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }

    override fun onRemainingLivesChanged(remainingLives: Int) {

        val remainingLivesResourceId = resources.getIdentifier("remaining_lives_left_3", "string", packageName)
        val remainingLivesLeft = if (remainingLivesResourceId != 0) {
            getString(remainingLivesResourceId)
        } else {
            getString(R.string.remaining_lives_left_3)
        }

        // there is a bug here. It doesn't display 3 at the start, but it does decrease when the mistake is made
        binding.textViewSudokuLives.text = "$remainingLivesLeft: $remainingLives"
        
        if (remainingLives == 0) {
            score = 0
            stopTimer()
            showProgressDialogOnGameFinish()
            moveToNextActivityWithDelay()
        }
    }

    override fun onGameFinished() {
        score =+ 20
        stopTimer()
        showProgressDialogOnGameFinished()
        moveToNextActivityWithDelay()

    }


    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                showProgressDialogOnTimeout()
                moveToNextActivityWithDelay()
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
                val message = "$secondsRemaining     $messageScore: $score"
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


    private fun moveToNextActivityWithDelay() {
        val delayMillis = 5000L
        val handler = Handler()

        handler.postDelayed({
            finish()
        }, delayMillis)
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnTimeout() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("sudoku_message_on_timeout", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.sudoku_message_on_timeout)
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

        val messageResourceId = resources.getIdentifier("sudoku_message_out_of_lives", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.sudoku_message_out_of_lives)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogOnGameFinished() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("sudoku_message_congrats", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.sudoku_message_congrats)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title_sudoku)
        actionBar?.setDisplayHomeAsUpEnabled(true)


        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Sudoku"
        titleTextView?.gravity = Gravity.CENTER
    }

    private fun showConfirmationDialog() {

        val yesResourceId = resources.getIdentifier("yes", "string", packageName)
        val noResourceId = resources.getIdentifier("no", "string", packageName)
        val yes = if (yesResourceId != 0) {
            getString(yesResourceId)
        } else {
            getString(R.string.yes)
        }
        val no = if (noResourceId != 0) {
            getString(noResourceId)
        } else {
            getString(R.string.no)
        }

        val messageId = resources.getIdentifier("sudoku_confirmation_message", "string", packageName)
        val message = if (messageId != 0) {
            getString(messageId)
        } else {
            getString(R.string.sudoku_confirmation_message)
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton(yes) { _, _ ->
            onBackPressed()
            finish()
        }
        builder.setNegativeButton(no) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                showConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        val messageResourceId = resources.getIdentifier("on_back_pressed_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.on_back_pressed_message)
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
                finish()
            }
            .setNegativeButton(no) { _, _ ->
                // Do nothing
            }
            .show()

    }

    private fun startBackgroundMusic() {
        MediaPlayerManager.initMediaPlayer(this, R.raw.sample_music)

        MediaPlayerManager.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
        MediaPlayerManager.release()
    }

    override fun onStop() {
        super.onStop()
        MediaPlayerManager.pause()
    }

    override fun onResume() {
        super.onResume()
        MediaPlayerManager.start()
    }
}