package com.deksi.graduationquiz.slagalica.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivitySpojniceBinding
import com.deksi.graduationquiz.slagalica.api.SpojniceApiService
import com.deksi.graduationquiz.slagalica.model.SpojniceModel
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

class Spojnice : AppCompatActivity() {

    private lateinit var binding: ActivitySpojniceBinding
    private var spojniceResponse: SpojniceModel? = null
    private var selectedLeft: String? = null
    private var selectedRight: String? = null
    private var timeLeft: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var totalScore: Int = 0
    private var roundScore: Int = 0
    private var currentRound = 2
    private val connectedPairs: MutableSet<Pair<String, String>> = mutableSetOf()
    private val correctConnections: List<Pair<String, String>> = listOf(
        "button_left1" to "button_right5",
        "button_left2" to "button_right4",
        "button_left3" to "button_right2",
        "button_left4" to "button_right3",
        "button_left5" to "button_right1",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpojniceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalScore = savedInstanceState?.getInt("totalScore") ?: 0
        savedInstanceState?.let {
            currentRound++
        }

        getRoundData()
        onClickListeners()
        onCreateProgressDialog()
        setUpActionBar()
    }

    private fun onClickListeners() {

        binding.buttonLeft1.setOnClickListener {
            selectedLeft = "button_left1"
            checkConnection()
        }

        binding.buttonLeft2.setOnClickListener {
            selectedLeft = "button_left2"
            checkConnection()
        }
        binding.buttonLeft3.setOnClickListener {
            selectedLeft = "button_left3"
            checkConnection()
        }

        binding.buttonLeft4.setOnClickListener {
            selectedLeft = "button_left4"
            checkConnection()
        }

        binding.buttonLeft5.setOnClickListener {
            selectedLeft = "button_left5"
            checkConnection()
        }

        binding.buttonRight1.setOnClickListener {
            selectedRight = "button_right1"
            checkConnection()
        }

        binding.buttonRight2.setOnClickListener {
            selectedRight = "button_right2"
            checkConnection()
        }

        binding.buttonRight3.setOnClickListener {
            selectedRight = "button_right3"
            checkConnection()
        }

        binding.buttonRight4.setOnClickListener {
            selectedRight = "button_right4"
            checkConnection()
        }

        binding.buttonRight5.setOnClickListener {
            selectedRight = "button_right5"
            checkConnection()
        }

    }

    private fun checkConnection() {
        if (selectedLeft != null && selectedRight != null) {
            val selectedPair = Pair(selectedLeft!!, selectedRight!!)
            connectedPairs.add(selectedPair)

            if (correctConnections.contains(selectedPair)) {
                // Correct connection logic
                changeButtonColorToGreen(selectedLeft)
                changeButtonColorToGreen(selectedRight)
                roundScore += 2

            } else {
                // Incorrect connection logic
                changeButtonColorToRed(selectedLeft)
                changeButtonColorToRed(selectedRight)

            }

            // Disable the selected buttons
            disableButton(selectedLeft)
            disableButton(selectedRight)

            // Reset selected items for the next turn
            selectedLeft = null
            selectedRight = null
        }

        if (connectedPairs.size == correctConnections.size) {

            if(currentRound < 3){
                stopTimer()
                showProgressDialogOnNextRound()
                moveToNextActivityWithDelay()
            }
            else {
                stopTimer()
                showProgressDialogOnGameFinish()
                moveToNextActivityWithDelay()
            }

        }
    }

    private fun findButtonByIdentifier(identifier: String?): Button? {
        return when (identifier) {
            "button_left1" -> binding.buttonLeft1
            "button_left2" -> binding.buttonLeft2
            "button_left3" -> binding.buttonLeft3
            "button_left4" -> binding.buttonLeft4
            "button_left5" -> binding.buttonLeft5
            "button_right1" -> binding.buttonRight1
            "button_right2" -> binding.buttonRight2
            "button_right3" -> binding.buttonRight3
            "button_right4" -> binding.buttonRight4
            "button_right5" -> binding.buttonRight5

            else -> null
        }
    }

    private fun disableButton(buttonId: String?) {
        val button = findButtonByIdentifier(buttonId)
        button?.isEnabled = false
    }

    private fun changeButtonColorToRed(buttonId: String?) {
        val button = findButtonByIdentifier(buttonId)
        button?.setBackgroundResource(R.drawable.round_red_reveal)
    }

    private fun changeButtonColorToGreen(buttonId: String?) {
        val button = findButtonByIdentifier(buttonId)
        button?.setBackgroundResource(R.drawable.round_green_reveal)
    }


    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(30000, 1000) {
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
                // mozda treba izmeniti
                val score = roundScore + totalScore
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

    private fun onCreateProgressDialog() {
        getSavedLanguageBySharedPreferences()

        val titleResourceId = resources.getIdentifier("spojnice_title_on_create", "string", packageName)
        val title = if (titleResourceId != 0) {
            getString(titleResourceId)
        } else {
            getString(R.string.spojnice_title_on_create)
        }

        val messageResourceId = resources.getIdentifier("spojnice_message_on_create", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.spojnice_message_on_create)
        }

        progressDialog = ProgressDialog.show(this, title, message, true, false)
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnTimeout() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("spojnice_message_time_up", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.spojnice_message_time_up)
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

        val messageResourceId = resources.getIdentifier("spojnice_message_on_finish", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.spojnice_message_on_finish)
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

        val messageResourceId = resources.getIdentifier("spojnice_message_on_round_starting", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId, currentRound)
        } else {
            getString(R.string.spojnice_message_on_round_starting)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun moveToNextActivityWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            if(currentRound < 3) {
                saveTotalScoreToLocalPreferences()
                onSaveInstanceState(Bundle())
                recreate()
                connectedPairs.clear()
            }

            else {
                saveTotalScoreToLocalPreferences()
                val intent = Intent(this@Spojnice, Asocijacije::class.java)
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
        val newTotalScore = currentTotalScore + roundScore

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

    private fun setupData() {
        val subject = binding.buttonSubject
        val left1 = binding.buttonLeft1
        val left2 = binding.buttonLeft2
        val left3 = binding.buttonLeft3
        val left4 = binding.buttonLeft4
        val left5 = binding.buttonLeft5
        val right1 = binding.buttonRight1
        val right2 = binding.buttonRight2
        val right3 = binding.buttonRight3
        val right4 = binding.buttonRight4
        val right5 = binding.buttonRight5

        subject.text = spojniceResponse?.subject
        left1.text = spojniceResponse?.left1
        left2.text = spojniceResponse?.left2
        left3.text = spojniceResponse?.left3
        left4.text = spojniceResponse?.left4
        left5.text = spojniceResponse?.left5
        right1.text = spojniceResponse?.right1
        right2.text = spojniceResponse?.right2
        right3.text = spojniceResponse?.right3
        right4.text = spojniceResponse?.right4
        right5.text = spojniceResponse?.right5
    }

    private fun getRoundData() {

        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"

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
            .baseUrl("https://192.168.1.9:8080/api/spojnice/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val spojniceService = retrofit.create(SpojniceApiService::class.java)

        val call = spojniceService.getRandomRound(savedLanguage)

        call.enqueue(object : Callback<SpojniceModel> {
            override fun onResponse(call: Call<SpojniceModel>, response: Response<SpojniceModel>) {
                if (response.isSuccessful) {
                    spojniceResponse = response.body()
                    setupData()
                    dismissProgressDialog()
                    initTimer()

                } else {
                    Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<SpojniceModel>, t: Throwable) {
                Toast.makeText(applicationContext, "API call failed", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun getSavedLanguageBySharedPreferences(){
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title)

        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Spojnice"
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