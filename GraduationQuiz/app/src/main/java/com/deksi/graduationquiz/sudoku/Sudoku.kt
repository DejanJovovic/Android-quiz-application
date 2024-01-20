package com.deksi.graduationquiz.sudoku


import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySudokuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTimer()
        setUpActionBar()

        binding.sudokuBoardView.registerListener(this)


        viewModel = ViewModelProvider(this)[SudokuViewModel::class.java]
        viewModel.sudokuGame = SudokuGame(listener = this)
        // observing what's happening to the selecetedCellData
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })

        val buttons = listOf(binding.buttonOne, binding.buttonTwo, binding.buttonThree, binding.buttonFour, binding.buttonFive, binding.buttonSix,
            binding.buttonSeven, binding.buttonEight, binding.buttonNine)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1) }
        }
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
        binding.textViewSudokuLives.text = "Remaining lives left: $remainingLives"
        
        if (remainingLives == 0) {
            stopTimer()
            showProgressDialogOnGameFinish()
            moveToNextActivityWithDelay()
        }
    }

    override fun onGameFinished() {
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
                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining     Score: "
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
            val intent = Intent(this@Sudoku, HomeActivity::class.java)
            startActivity(intent)

            finish()
        }, delayMillis)
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnTimeout() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Time is up!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogOnGameFinish() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("You are out of lives!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogOnGameFinished() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Congrats on beating the game!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title)

        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Sudoku"
        titleTextView?.gravity = Gravity.CENTER
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
    }
}