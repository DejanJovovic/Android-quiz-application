package com.deksi.graduationquiz.slagalica.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityKorakPoKorakBinding
import com.deksi.graduationquiz.slagalica.api.KorakPoKorakApiService
import com.deksi.graduationquiz.slagalica.model.KorakPoKorakModel
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

class KorakPoKorak : AppCompatActivity() {

    private lateinit var binding: ActivityKorakPoKorakBinding
    private var korakPoKorakResponse: KorakPoKorakModel? = null
    private var konacno: EditText? = null
    private var timeLeft: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private var currentRound = 2
    private var totalScore: Int = 0
    private var currentHintIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKorakPoKorakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            currentRound++
        }

        getRoundData()
        findViewsById()
        onClickListeners()
        onCreateProgressDialog()
        setUpActionBar()
    }

    private fun findViewsById() {
        konacno = findViewById(R.id.edit_text_konacno_korak_po_korak)
    }

    private fun onClickListeners() {
        binding.buttonHint1.setOnClickListener { onButtonClick(binding.buttonHint1, 1) }
        binding.buttonHint2.setOnClickListener { onButtonClick(binding.buttonHint2, 2) }
        binding.buttonHint3.setOnClickListener { onButtonClick(binding.buttonHint3, 3) }
        binding.buttonHint4.setOnClickListener { onButtonClick(binding.buttonHint4, 4) }
        binding.buttonHint5.setOnClickListener { onButtonClick(binding.buttonHint5, 5) }
        binding.buttonHint6.setOnClickListener { onButtonClick(binding.buttonHint6, 6) }
        binding.buttonHint7.setOnClickListener { onButtonClick(binding.buttonHint7, 7) }
        binding.buttonSubmitKorakpokorak.setOnClickListener { updateKorakPoKorak() }
    }

    private fun onButtonClick(button: Button, hintIndex: Int) {
        if (currentHintIndex + 1 == hintIndex && korakPoKorakResponse != null) {

            val buttonText = when (hintIndex) {
                1 -> korakPoKorakResponse?.hint1
                2 -> korakPoKorakResponse?.hint2
                3 -> korakPoKorakResponse?.hint3
                4 -> korakPoKorakResponse?.hint4
                5 -> korakPoKorakResponse?.hint5
                6 -> korakPoKorakResponse?.hint6
                7 -> korakPoKorakResponse?.hint7
                else -> null
            }



            buttonText?.let {
                button.text = it
                currentHintIndex++
            }
        }
    }


    private fun checkAndUpdateField(editText: EditText?, expectedValue: String) {
        if (editText?.text.toString() == expectedValue) {
            editText?.setBackgroundResource(R.drawable.round_green_reveal)


            // Calculate the score based on the hints revealed
            totalScore = when (currentHintIndex) {
                1 -> 20
                2 -> 18
                3 -> 16
                4 -> 14
                5 -> 12
                6 -> 10
                7 -> 8
                else -> 0
            }



            // Automatically show corresponding values when the user enters the right value
            when (editText) {
                konacno -> {
                    showValues(
                        korakPoKorakResponse?.hint1,
                        korakPoKorakResponse?.hint2,
                        korakPoKorakResponse?.hint3,
                        korakPoKorakResponse?.hint4,
                        korakPoKorakResponse?.hint5,
                        korakPoKorakResponse?.hint6,
                        korakPoKorakResponse?.hint7,
                    )
                }
            }
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
        } else {
            Toast.makeText(applicationContext, "Wrong!", Toast.LENGTH_LONG).show()
        }
    }

    private fun showValues(vararg values: String?) {
        if (values.size >= 7) {
            // Set text on corresponding buttons
            binding.buttonHint1.text = values[0]
            binding.buttonHint2.text = values[1]
            binding.buttonHint3.text = values[2]
            binding.buttonHint4.text = values[3]
            binding.buttonHint5.text = values[4]
            binding.buttonHint6.text = values[5]
            binding.buttonHint7.text = values[6]

        } else {
            Log.e("showValues", "Not enough non-null values provided")
        }

    }

    private fun updateKorakPoKorak() {
        val getKonacno: String? = korakPoKorakResponse?.konacno

        if (!konacno?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacno, getKonacno ?: "")
        }
    }

    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(70000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
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

                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining     Score: $totalScore"
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
        progressDialog = ProgressDialog.show(this, "Please wait", "Loading game..", true, false)

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
        progressDialog!!.setTitle("Game is finished. Please wait..")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogOnNextRound() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Round $currentRound is starting...")
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
                onSaveInstanceState(Bundle())
                recreate()
                clearKonacnoField()
            }

            else {
                val intent = Intent(this@KorakPoKorak, MojBroj::class.java)
                startActivity(intent)
                finish()
            }

        }, delayMilis)
    }

    private fun clearKonacnoField() {
        konacno?.text?.clear()
    }

    private fun displayValuesOnTimeout() {
        if (korakPoKorakResponse != null) {
            showValues(
                korakPoKorakResponse?.hint1,
                korakPoKorakResponse?.hint2,
                korakPoKorakResponse?.hint3,
                korakPoKorakResponse?.hint4,
                korakPoKorakResponse?.hint5,
                korakPoKorakResponse?.hint6,
                korakPoKorakResponse?.hint7
            )
        } else {
            Log.e("displayValuesOnTimeout", "korakPoKorakResponse is null")
        }
    }


    private fun getRoundData() {

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
            .baseUrl("https://192.168.1.9:8080/api/korakPoKorak/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val korakPoKorakService = retrofit.create(KorakPoKorakApiService::class.java)

        val call = korakPoKorakService.getRandomRound()

        call.enqueue(object : Callback<KorakPoKorakModel> {
            override fun onResponse(
                call: Call<KorakPoKorakModel>,
                response: Response<KorakPoKorakModel>
            ) {
                if (response.isSuccessful) {
                    korakPoKorakResponse = response.body()
                    dismissProgressDialog()
                    initTimer()

                } else {
                    Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<KorakPoKorakModel>, t: Throwable) {
                Toast.makeText(applicationContext, "API call failed", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar

        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.action_bar_custom_title)

        val titleTextView =
            actionBar?.customView?.findViewById<TextView>(R.id.text_view_custom_title)

        titleTextView?.text = "Korak po korak"
        titleTextView?.gravity = Gravity.CENTER
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft?.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the state
        outState.putInt("currentRound", currentRound)
    }
}