package com.deksi.graduationquiz.slagalica.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.R
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
    private var totalScore = 0  // treba dodati poene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsocijacijeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.buttonA1.setOnClickListener { onButtonClick(binding.buttonA1, "a1") }
        binding.buttonA2.setOnClickListener { onButtonClick(binding.buttonA2, "a2") }
        binding.buttonA3.setOnClickListener { onButtonClick(binding.buttonA3, "a3") }
        binding.buttonA4.setOnClickListener { onButtonClick(binding.buttonA4, "a4") }
        binding.buttonB1.setOnClickListener { onButtonClick(binding.buttonB1, "b1") }
        binding.buttonB2.setOnClickListener { onButtonClick(binding.buttonB2, "b2") }
        binding.buttonB3.setOnClickListener { onButtonClick(binding.buttonB3, "b3") }
        binding.buttonB4.setOnClickListener { onButtonClick(binding.buttonB4, "b4") }
        binding.buttonC1.setOnClickListener { onButtonClick(binding.buttonC1, "c1") }
        binding.buttonC2.setOnClickListener { onButtonClick(binding.buttonC2, "c2") }
        binding.buttonC3.setOnClickListener { onButtonClick(binding.buttonC3, "c3") }
        binding.buttonC4.setOnClickListener { onButtonClick(binding.buttonC4, "c4") }
        binding.buttonD1.setOnClickListener { onButtonClick(binding.buttonD1, "d1") }
        binding.buttonD2.setOnClickListener { onButtonClick(binding.buttonD2, "d2") }
        binding.buttonD3.setOnClickListener { onButtonClick(binding.buttonD3, "d3") }
        binding.buttonD4.setOnClickListener { onButtonClick(binding.buttonD4, "d4") }
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
    }

    private fun checkAndUpdateField(editText: EditText?, expectedValue: String, points: Int) {
        if (editText?.text.toString() == expectedValue) {
            editText?.setBackgroundResource(R.drawable.round_green_reveal)
            // treba menjati
//            Toast.makeText(applicationContext, "Osvojili ste $points poena", Toast.LENGTH_LONG)
//                .show()

            // Automatically show corresponding values when the user enters the right value
            when (editText) {
                konacnoA -> {
                    showValues(
                        asocijacijeResponse?.a1,
                        asocijacijeResponse?.a2,
                        asocijacijeResponse?.a3,
                        asocijacijeResponse?.a4
                    )
                }

                konacnoB -> {
                    showValues(
                        asocijacijeResponse?.b1,
                        asocijacijeResponse?.b2,
                        asocijacijeResponse?.b3,
                        asocijacijeResponse?.b4
                    )
                }

                konacnoC -> {
                    showValues(
                        asocijacijeResponse?.c1,
                        asocijacijeResponse?.c2,
                        asocijacijeResponse?.c3,
                        asocijacijeResponse?.c4
                    )
                }

                konacnoD -> {
                    showValues(
                        asocijacijeResponse?.d1,
                        asocijacijeResponse?.d2,
                        asocijacijeResponse?.d3,
                        asocijacijeResponse?.d4
                    )
                }

                konacno -> {
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
            Toast.makeText(applicationContext, "Wrong!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateAsosijacije() {
        val getKonacnoA: String? = asocijacijeResponse?.konacnoA
        val getKonacnoB: String? = asocijacijeResponse?.konacnoB
        val getKonacnoC: String? = asocijacijeResponse?.konacnoC
        val getKonacnoD: String? = asocijacijeResponse?.konacnoD
        val getKonacno: String? = asocijacijeResponse?.konacno

        if (!konacnoA?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoA, getKonacnoA ?: "", 5)
        }
        if (!konacnoB?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoB, getKonacnoB ?: "", 5)
        }
        if (!konacnoC?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoC, getKonacnoC ?: "", 5)
        }
        if (!konacnoD?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacnoD, getKonacnoD ?: "", 5)
        }
        if (!konacno?.text.isNullOrEmpty()) {
            checkAndUpdateField(konacno, getKonacno ?: "", 20)
            if (konacno?.text.toString() == getKonacno) {
                stopTimer()
                showProgressDialogOnGameFinish()
                moveToNextActivityWithDelay()
            }

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

    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(120000, 1000) {
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
                progressDialog?.setMessage("$secondsRemaining")

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

    private fun moveToNextActivityWithDelay() {
        val delayMillis = 5000L
        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this@Asocijacije, Skocko::class.java)
            startActivity(intent)

            finish()
        }, delayMillis)
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
            .baseUrl("https://192.168.197.66:8080/api/slagalica/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val asosijacijeService = retrofit.create(AsocijacijeApiService::class.java)

        val call = asosijacijeService.getRandomRound()

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

}