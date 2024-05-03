package com.deksi.graduationquiz.slagalica.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.mediaPlayer.MediaPlayerManager
import com.deksi.graduationquiz.databinding.ActivityAsocijacijeBinding
import com.deksi.graduationquiz.slagalica.api.AsocijacijeApiService
import com.deksi.graduationquiz.slagalica.model.AsocijacijeModel
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

class Asocijacije : AppCompatActivity() {

    private lateinit var binding: ActivityAsocijacijeBinding
    private var asocijacijeResponse: AsocijacijeModel? = null
    private var konacnoA: EditText? = null
    private var konacnoB: EditText? = null
    private var konacnoC: EditText? = null
    private var konacnoD: EditText? = null
    private var konacno: EditText? = null
    private var timeLeft: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var currentRound = 2
    private var pointsA: Int = 0
    private var pointsB: Int = 0
    private var pointsC: Int = 0
    private var pointsD: Int = 0
    private var userPoints: Int = 0
    private var totalScore: Int = 0
    private var konacnoAGuessed = false
    private var konacnoBGuessed = false
    private var konacnoCGuessed = false
    private var konacnoDGuessed = false
    private var konacnoGuessed = false
    private var openedCluesA: Int = 4
    private var openedCluesB: Int = 4
    private var openedCluesC: Int = 4
    private var openedCluesD: Int = 4



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsocijacijeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalScore = savedInstanceState?.getInt("totalScore") ?: 0
        savedInstanceState?.let {
            currentRound++
        }

        getRoundData()
        setOnClickButtonListeners()
        findViewsById()
        onCreateProgressDialog()
        setUpActionBar()

    }

    private fun findViewsById() {
        konacnoA = findViewById(R.id.edit_text_konacnoA)
        konacnoB = findViewById(R.id.edit_text_konacnoB)
        konacnoC = findViewById(R.id.edit_text_konacnoC)
        konacnoD = findViewById(R.id.edit_text_konacnoD)
        konacno = findViewById(R.id.edit_text_konacno)
    }

    private fun setOnClickButtonListeners() {
        binding.buttonA1.setOnClickListener {
            openedCluesA--
            onButtonClick(binding.buttonA1, "a1") }
        binding.buttonA2.setOnClickListener {
            openedCluesA--
            onButtonClick(binding.buttonA2, "a2") }
        binding.buttonA3.setOnClickListener {
            openedCluesA--
            onButtonClick(binding.buttonA3, "a3") }
        binding.buttonA4.setOnClickListener {
            openedCluesA--
            onButtonClick(binding.buttonA4, "a4") }
        binding.buttonB1.setOnClickListener {
            openedCluesB--
            onButtonClick(binding.buttonB1, "b1") }
        binding.buttonB2.setOnClickListener {
            openedCluesB--
            onButtonClick(binding.buttonB2, "b2") }
        binding.buttonB3.setOnClickListener {
            openedCluesB--
            onButtonClick(binding.buttonB3, "b3") }
        binding.buttonB4.setOnClickListener {
            openedCluesB--
            onButtonClick(binding.buttonB4, "b4") }
        binding.buttonC1.setOnClickListener {
            openedCluesC--
            onButtonClick(binding.buttonC1, "c1") }
        binding.buttonC2.setOnClickListener {
            openedCluesC--
            onButtonClick(binding.buttonC2, "c2") }
        binding.buttonC3.setOnClickListener {
            openedCluesC--
            onButtonClick(binding.buttonC3, "c3") }
        binding.buttonC4.setOnClickListener {
            openedCluesC--
            onButtonClick(binding.buttonC4, "c4") }
        binding.buttonD1.setOnClickListener {
            openedCluesD--
            onButtonClick(binding.buttonD1, "d1") }
        binding.buttonD2.setOnClickListener {
            openedCluesD--
            onButtonClick(binding.buttonD2, "d2") }
        binding.buttonD3.setOnClickListener {
            openedCluesD--
            onButtonClick(binding.buttonD3, "d3") }
        binding.buttonD4.setOnClickListener {
            openedCluesD--
            onButtonClick(binding.buttonD4, "d4") }
        val buttonSubmit = binding.buttonSubmit
        buttonSubmit.setOnClickListener {
            buttonSubmit.isEnabled = true
            updateAsosijacije()
        }
    }

    private fun onButtonClick(button: Button, buttonId: String) {
        if (asocijacijeResponse != null) {
            // Get the corresponding value based on the buttonId
            val buttonText = when (buttonId) {
                "a1" -> asocijacijeResponse?.a1
                "a2" -> asocijacijeResponse?.a2
                "a3" -> asocijacijeResponse?.a3
                "a4" -> asocijacijeResponse?.a4
                "b1" -> asocijacijeResponse?.b1
                "b2" -> asocijacijeResponse?.b2
                "b3" -> asocijacijeResponse?.b3
                "b4" -> asocijacijeResponse?.b4
                "c1" -> asocijacijeResponse?.c1
                "c2" -> asocijacijeResponse?.c2
                "c3" -> asocijacijeResponse?.c3
                "c4" -> asocijacijeResponse?.c4
                "d1" -> asocijacijeResponse?.d1
                "d2" -> asocijacijeResponse?.d2
                "d3" -> asocijacijeResponse?.d3
                "d4" -> asocijacijeResponse?.d4
                else -> null
            }
            buttonText?.let { button.text = it }
        }

        if(buttonId.startsWith("a")){
            binding.editTextKonacnoA.isEnabled = true
        }
        else if(buttonId.startsWith("b")){
            binding.editTextKonacnoB.isEnabled = true
        }
        else if(buttonId.startsWith("c")){
            binding.editTextKonacnoC.isEnabled = true
        }
        else if(buttonId.startsWith("d")){
            binding.editTextKonacnoD.isEnabled = true
        }
    }

    private fun checkAndUpdateField(editText: EditText?, expectedValue: String) {
        if (editText?.text.toString().trim().equals(expectedValue.trim(), ignoreCase = true)) {
            // Check if the editText is already revealed (has the green background)
            if (editText == konacno && !konacnoGuessed ||
                editText == konacnoA && !konacnoAGuessed ||
                editText == konacnoB && !konacnoBGuessed ||
                editText == konacnoC && !konacnoCGuessed ||
                editText == konacnoD && !konacnoDGuessed
            ) {
                editText?.setBackgroundResource(R.drawable.round_green_reveal)


                editText?.isEnabled = false

                when (editText) {
                    konacnoA -> {
                        binding.buttonA1.text = asocijacijeResponse?.a1
                        binding.buttonA2.text = asocijacijeResponse?.a2
                        binding.buttonA3.text = asocijacijeResponse?.a3
                        binding.buttonA4.text = asocijacijeResponse?.a4
                        pointsA += 2
                        konacnoAGuessed = true
                        binding.editTextKonacno.isEnabled = true

                    }

                    konacnoB -> {
                        binding.buttonB1.text = asocijacijeResponse?.b1
                        binding.buttonB2.text = asocijacijeResponse?.b2
                        binding.buttonB3.text = asocijacijeResponse?.b3
                        binding.buttonB4.text = asocijacijeResponse?.b4
                        pointsB += 2
                        konacnoBGuessed = true
                        binding.editTextKonacno.isEnabled = true


                    }

                    konacnoC -> {
                        binding.buttonC1.text = asocijacijeResponse?.c1
                        binding.buttonC2.text = asocijacijeResponse?.c2
                        binding.buttonC3.text = asocijacijeResponse?.c3
                        binding.buttonC4.text = asocijacijeResponse?.c4
                        pointsC += 2
                        konacnoCGuessed = true
                        binding.editTextKonacno.isEnabled = true

                    }

                    konacnoD -> {
                        binding.buttonD1.text = asocijacijeResponse?.d1
                        binding.buttonD2.text = asocijacijeResponse?.d2
                        binding.buttonD3.text = asocijacijeResponse?.d3
                        binding.buttonD4.text = asocijacijeResponse?.d4
                        pointsD += 2
                        konacnoDGuessed = true
                        binding.editTextKonacno.isEnabled = true

                    }

                    konacno -> {
                        konacnoGuessed = true
                        konacnoAGuessed = true
                        konacnoBGuessed = true
                        konacnoCGuessed = true
                        konacnoDGuessed = true
                        calculatingPointsWhenKonacnoIsGuessed()
                        userPoints += 7
                        konacnoA?.setBackgroundResource(R.drawable.round_green_reveal)
                        konacnoB?.setBackgroundResource(R.drawable.round_green_reveal)
                        konacnoC?.setBackgroundResource(R.drawable.round_green_reveal)
                        konacnoD?.setBackgroundResource(R.drawable.round_green_reveal)
                        showValues(
                            asocijacijeResponse?.a1,
                            asocijacijeResponse?.a2,
                            asocijacijeResponse?.a3,
                            asocijacijeResponse?.a4,
                            asocijacijeResponse?.b1,
                            asocijacijeResponse?.b2,
                            asocijacijeResponse?.b3,
                            asocijacijeResponse?.b4,
                            asocijacijeResponse?.c1,
                            asocijacijeResponse?.c2,
                            asocijacijeResponse?.c3,
                            asocijacijeResponse?.c4,
                            asocijacijeResponse?.d1,
                            asocijacijeResponse?.d2,
                            asocijacijeResponse?.d3,
                            asocijacijeResponse?.d4,
                            asocijacijeResponse?.konacnoA,
                            asocijacijeResponse?.konacnoB,
                            asocijacijeResponse?.konacnoC,
                            asocijacijeResponse?.konacnoD
                        )
                    }
                }

            } else {
                // The field was already revealed, no need to add points again
//                Toast.makeText(applicationContext, "Already Guessed!", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "Wrong!", Toast.LENGTH_LONG).show()
        }
    }

    private fun showValues(vararg values: String?) {
        if (values.size >= 20) {
            // Set text on corresponding buttons
            binding.buttonA1.text = values[0]
            binding.buttonA2.text = values[1]
            binding.buttonA3.text = values[2]
            binding.buttonA4.text = values[3]
            binding.buttonB1.text = values[4]
            binding.buttonB2.text = values[5]
            binding.buttonB3.text = values[6]
            binding.buttonB4.text = values[7]
            binding.buttonC1.text = values[8]
            binding.buttonC2.text = values[9]
            binding.buttonC3.text = values[10]
            binding.buttonC4.text = values[11]
            binding.buttonD1.text = values[12]
            binding.buttonD2.text = values[13]
            binding.buttonD3.text = values[14]
            binding.buttonD4.text = values[15]

            // Set text on corresponding EditText fields
            binding.editTextKonacnoA.text = Editable.Factory.getInstance().newEditable(values[16])
            binding.editTextKonacnoB.text = Editable.Factory.getInstance().newEditable(values[17])
            binding.editTextKonacnoC.text = Editable.Factory.getInstance().newEditable(values[18])
            binding.editTextKonacnoD.text = Editable.Factory.getInstance().newEditable(values[19])


        } else {
            Log.e("showValues", "Not enough non-null values provided")
        }
    }

    private fun updateAsosijacije() {
        val getKonacnoA: String? = asocijacijeResponse?.konacnoA
        val getKonacnoB: String? = asocijacijeResponse?.konacnoB
        val getKonacnoC: String? = asocijacijeResponse?.konacnoC
        val getKonacnoD: String? = asocijacijeResponse?.konacnoD
        val getKonacno: String? = asocijacijeResponse?.konacno

        if (!konacnoA?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoA, getKonacnoA ?: "")
        }
        if (!konacnoB?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoB, getKonacnoB ?: "")
        }
        if (!konacnoC?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoC, getKonacnoC ?: "")
        }
        if (!konacnoD?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoD, getKonacnoD ?: "")
        }
        if (!konacno?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacno, getKonacno ?: "")
            if (konacno?.text.toString() == getKonacno) {
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
    }


    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                calculatingPointsOnTimeout()
                showProgressDialogOnTimeout()
                moveToNextActivityWithDelay()
                displayValuesOnTimeout()
            }
        }
        (timeLeft as CountDownTimer).start()
    }

    private fun startTimerProgressDialog() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (totalTime - millisUntilFinished).toInt()
                calculatingPointsOnTimeout()

                val messageResourceId = resources.getIdentifier("timer_score", "string", packageName)
                val messageScore = if (messageResourceId != 0) {
                    getString(messageResourceId)
                } else {
                    getString(R.string.timer_score)
                }

                val messageScoreSpannable = SpannableString(messageScore).apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                val secondsRemaining = millisUntilFinished / 1000
                totalScore = pointsA + openedCluesA + pointsB + openedCluesB + pointsC + openedCluesC + pointsD + openedCluesD + userPoints
//                Toast.makeText(applicationContext, "pA: $pointsA," +
//                        "cA: $openedCluesA," +
//                        "pB: $pointsB," +
//                        "cB: $openedCluesB," +
//                        "pC: $pointsC," +
//                        "CC: $openedCluesC, " +
//                        "pD: $pointsD," +
//                        "cD: $openedCluesD," +
//                        "uP: $userPoints",Toast.LENGTH_LONG).show()
                val message = "$secondsRemaining     $messageScoreSpannable: $totalScore"
                progressDialog?.setMessage(message)

            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun calculatingPointsWhenKonacnoIsGuessed() {
        if(pointsA == 0) {
            pointsA += 2
        }
        if(pointsB == 0) {
            pointsB += 2
        }
        if(pointsC == 0) {
            pointsC += 2
        }
        if(pointsD == 0) {
            pointsD += 2
        }
    }

    private fun calculatingPointsOnTimeout() {
        if(openedCluesA == 4) {
            if(pointsA == 0) {
                if(userPoints == 0) {
                    openedCluesA = 0
                }
            }

        }
        if(openedCluesB == 4) {
            if(pointsB == 0) {
                if(userPoints == 0) {
                    openedCluesB = 0
                }
            }

        }
        if(openedCluesC == 4) {
            if(pointsC == 0) {
                if(userPoints == 0) {
                    openedCluesC = 0
                }
            }

        }
        if(openedCluesD == 4) {
            if(pointsD == 0) {
                if(userPoints == 0) {
                    openedCluesD = 0
                }
            }

        }
    }

    private fun stopTimer() {
        timeLeft?.cancel()
    }

    private fun onCreateProgressDialog() {

        getSavedLanguageBySharedPreferences()

        val titleResourceId = resources.getIdentifier("asocijacije_title_on_create", "string", packageName)
        val title = if (titleResourceId != 0) {
            getString(titleResourceId)
        } else {
            getString(R.string.asocijacije_title_on_create)
        }

        val messageResourceId = resources.getIdentifier("asocijacije_message_on_create", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.asocijacije_message_on_create)
        }

        val titleSpannable = SpannableString(title).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog.show(this, titleSpannable, messageSpannable, true, false)

    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnTimeout() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("asocijacije_message_time_up", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.asocijacije_message_time_up)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }


    private fun showProgressDialogOnGameFinish() {

        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("asocijacije_message_on_finish", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.asocijacije_message_on_finish)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogOnNextRound() {

        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("asocijacije_message_on_round_starting", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId, currentRound)
        } else {
            getString(R.string.asocijacije_message_on_round_starting)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
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
                clearKonacnoFields()
            }

            else {
                saveTotalScoreToLocalPreferences()
                val intent = Intent(this@Asocijacije, Skocko::class.java)
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


    private fun clearKonacnoFields() {
        konacnoA?.text?.clear()
        konacnoB?.text?.clear()
        konacnoC?.text?.clear()
        konacnoD?.text?.clear()
        konacno?.text?.clear()

        konacnoAGuessed = false
        konacnoBGuessed = false
        konacnoCGuessed = false
        konacnoDGuessed = false
        konacnoGuessed = false
    }

    private fun displayValuesOnTimeout() {
        if (asocijacijeResponse != null) {
            showValues(
                asocijacijeResponse?.a1,
                asocijacijeResponse?.a2,
                asocijacijeResponse?.a3,
                asocijacijeResponse?.a4,
                asocijacijeResponse?.b1,
                asocijacijeResponse?.b2,
                asocijacijeResponse?.b3,
                asocijacijeResponse?.b4,
                asocijacijeResponse?.c1,
                asocijacijeResponse?.c2,
                asocijacijeResponse?.c3,
                asocijacijeResponse?.c4,
                asocijacijeResponse?.d1,
                asocijacijeResponse?.d2,
                asocijacijeResponse?.d3,
                asocijacijeResponse?.d4,
                asocijacijeResponse?.konacnoA,
                asocijacijeResponse?.konacnoB,
                asocijacijeResponse?.konacnoC,
                asocijacijeResponse?.konacnoD
            )
        } else {
            Log.e("displayValuesOnTimeout", "asocijacijeResponse is null")
        }
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
            .baseUrl("https://192.168.1.10:8080/api/slagalica/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val asosijacijeService = retrofit.create(AsocijacijeApiService::class.java)

        val call = asosijacijeService.getRandomRound(savedLanguage)

        call.enqueue(object : Callback<AsocijacijeModel> {
            override fun onResponse(
                call: Call<AsocijacijeModel>,
                response: Response<AsocijacijeModel>
            ) {
                if (response.isSuccessful) {
                    asocijacijeResponse = response.body()
                    dismissProgressDialog()
                    initTimer()

                } else {
                    Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AsocijacijeModel>, t: Throwable) {
                Toast.makeText(applicationContext, "API call failed", Toast.LENGTH_SHORT).show()
            }

        })

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

        titleTextView?.text = "Asocijacije"
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

        val yesSpannable = SpannableString(yes).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noSpannable = SpannableString(no).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@Asocijacije, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        AlertDialog.Builder(this)
            .setMessage(messageSpannable)
            .setPositiveButton(yesSpannable) { _, _ ->
                super.onBackPressed()
                MediaPlayerManager.release()
                clearTotalScoreFromPreferences()
            }
            .setNegativeButton(noSpannable) { _, _ ->
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