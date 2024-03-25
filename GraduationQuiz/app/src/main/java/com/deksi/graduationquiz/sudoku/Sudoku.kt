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
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.deksi.graduationquiz.authentication.api.SudokuUserTimeService
import com.deksi.graduationquiz.authentication.api.UserScoreService
import com.deksi.graduationquiz.authentication.model.SudokuUserTime
import com.deksi.graduationquiz.authentication.model.UserScore
import com.deksi.graduationquiz.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivitySudokuBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.slagalica.activities.Skocko

import com.deksi.graduationquiz.sudoku.game.Cell
import com.deksi.graduationquiz.sudoku.game.SudokuGame
import com.deksi.graduationquiz.sudoku.view.SudokuBoardView
import com.deksi.graduationquiz.sudoku.viewModel.SudokuViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class Sudoku : AppCompatActivity(), SudokuBoardView.OnTouchListener, SudokuGame.SudokuGameListener {

    private lateinit var viewModel: SudokuViewModel
    private lateinit var binding: ActivitySudokuBinding
    private var time: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val countDownTime: Long = 5000
    private var elapsedTime: Int = 0

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

    private fun saveUsernameAndTotalTime() {

        val usernameSharedPreferences = getSharedPreferences("UsernamePref", Context.MODE_PRIVATE)
        val username = usernameSharedPreferences.getString("username", "")

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.134.66:8080/api/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val sudokuUserTimeService = retrofit.create(SudokuUserTimeService::class.java)
        val time = elapsedTime / 1000
        val userTime = SudokuUserTime(username!!, time)

        val call = sudokuUserTimeService.updateTime(userTime)

        call?.enqueue(object: Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if(response.isSuccessful){

                    Toast.makeText(applicationContext, "Score updated successfully", Toast.LENGTH_SHORT).show()
                } else {

                    val errorMessage = "Error: ${response.code()} ${response.message()}"
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(applicationContext, "API call failed", Toast.LENGTH_SHORT).show()
            }

        })

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

        val remainingLivesResourceId = resources.getIdentifier("remaining_lives_left", "string", packageName)
        val remainingLivesLeft = if (remainingLivesResourceId != 0) {
            getString(remainingLivesResourceId)
        } else {
            getString(R.string.remaining_lives_left)
        }

        val remainingLivesLeftSpannable = SpannableString(remainingLivesLeft).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.textViewSudokuLives.text = "$remainingLivesLeftSpannable: $remainingLives"
        
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
        val timerText = binding.textViewTime
        time = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedTime += 1000
                timerText.text = (elapsedTime / 1000).toString()
            }

            override fun onFinish() {
            }
        }
        (time as CountDownTimer).start()
    }

    private fun startTimerProgressDialog() {
        countDownTimer = object : CountDownTimer(countDownTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (countDownTime - millisUntilFinished).toInt()

                val messageResourceId = resources.getIdentifier("timer_time", "string", packageName)
                val messageTime = if (messageResourceId != 0) {
                    getString(messageResourceId)
                } else {
                    getString(R.string.timer_time)
                }

                val messageSecondsResourceId = resources.getIdentifier("timer_seconds", "string", packageName)
                val messageSeconds = if (messageSecondsResourceId != 0) {
                    getString(messageSecondsResourceId)
                } else {
                    getString(R.string.timer_seconds)
                }

                val messageTimeSpannable = SpannableString(messageTime).apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                val messageSecondsSpannable = SpannableString(messageSeconds).apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                val totalTime = elapsedTime / 1000
                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining     $messageTimeSpannable: $totalTime $messageSecondsSpannable"
                progressDialog?.setMessage(message)

            }

            override fun onFinish() {
                saveUsernameAndTotalTime()
                dismissProgressDialog()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun stopTimer() {
        time?.cancel()
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

    private fun showProgressDialogOnGameFinish() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("sudoku_message_out_of_lives", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.sudoku_message_out_of_lives)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = countDownTime.toInt()
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


        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = countDownTime.toInt()
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

        val yesSpannable = SpannableString(yes).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noSpannable = SpannableString(no).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        val builder = AlertDialog.Builder(this)
        builder.setMessage(messageSpannable)
        builder.setPositiveButton(yesSpannable) { _, _ ->
            onBackPressed()
            MediaPlayerManager.release()
            finish()
        }
        builder.setNegativeButton(noSpannable) { dialog, _ ->
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

        val yesSpannable = SpannableString(yes).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noSpannable = SpannableString(no).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Sudoku, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        AlertDialog.Builder(this)
            .setMessage(messageSpannable)
            .setPositiveButton(yesSpannable) { _, _ ->
                super.onBackPressed()
                finish()
            }
            .setNegativeButton(noSpannable) { _, _ ->
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
        time?.cancel()
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