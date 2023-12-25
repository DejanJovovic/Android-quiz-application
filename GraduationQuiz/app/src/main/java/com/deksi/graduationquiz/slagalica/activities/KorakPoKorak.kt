package com.deksi.graduationquiz.slagalica.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityKorakPoKorakBinding
import com.deksi.graduationquiz.slagalica.api.AsocijacijeApiService
import com.deksi.graduationquiz.slagalica.api.KorakPoKorakApiService
import com.deksi.graduationquiz.slagalica.model.AsocijacijeModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKorakPoKorakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRoundData()
        findViewsById()
        onClickListeners()
    }

    private fun onButtonClick(button: Button, buttonId: String) {
        if (korakPoKorakResponse != null) {
            // Get the corresponding value based on the buttonId
            val buttonText = when (buttonId) {
                "hint1" -> korakPoKorakResponse?.hint1
                "hint2" -> korakPoKorakResponse?.hint2
                "hint3" -> korakPoKorakResponse?.hint3
                "hint4" -> korakPoKorakResponse?.hint4
                "hint5" -> korakPoKorakResponse?.hint5
                "hint6" -> korakPoKorakResponse?.hint6
                "hint7" -> korakPoKorakResponse?.hint7
                else -> null
            }

            // Set the button text
            buttonText?.let { button.text = it }
        }
    }

    private fun onClickListeners() {
        binding.buttonHint1.setOnClickListener { onButtonClick(binding.buttonHint1, "hint1") }
        binding.buttonHint2.setOnClickListener { onButtonClick(binding.buttonHint2, "hint2") }
        binding.buttonHint3.setOnClickListener { onButtonClick(binding.buttonHint3, "hint3") }
        binding.buttonHint4.setOnClickListener { onButtonClick(binding.buttonHint4, "hint4") }
        binding.buttonHint5.setOnClickListener { onButtonClick(binding.buttonHint5, "hint5") }
        binding.buttonHint6.setOnClickListener { onButtonClick(binding.buttonHint6, "hint6") }
        binding.buttonHint7.setOnClickListener { onButtonClick(binding.buttonHint7, "hint7") }
        binding.buttonSubmitKorakpokorak.setOnClickListener { updateKorakPoKorak() }
    }

    private fun findViewsById() {
        konacno = findViewById(R.id.edit_text_konacno_korak_po_korak)
    }


    private fun checkAndUpdateField(editText: EditText?, expectedValue: String, points: Int) {
        if (editText?.text.toString() == expectedValue) {
            editText?.setBackgroundResource(R.drawable.round_green_reveal)
            Toast.makeText(applicationContext, "Osvojili ste $points poena", Toast.LENGTH_LONG)
                .show()

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
            moveToNextActivityWithDelay()


        } else {
            Toast.makeText(applicationContext, "Nije tacno", Toast.LENGTH_LONG).show()
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
            checkAndUpdateField(konacno, getKonacno ?: "", 20)
        }
    }

    private fun moveToNextActivityWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this@KorakPoKorak, MojBroj::class.java)
            startActivity(intent)

            finish()
        }, delayMilis)
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
            .baseUrl("https://192.168.197.66:8080/api/korakPoKorak/")
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
}