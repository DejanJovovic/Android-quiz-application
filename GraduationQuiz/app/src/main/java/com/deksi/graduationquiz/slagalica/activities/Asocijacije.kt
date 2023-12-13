package com.deksi.graduationquiz.slagalica.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAsocijacijeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        getRoundData()
        setOnClickButtonListeners()

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

            // Set the button text
            buttonText?.let { button.text = it }
        }
    }


    private fun getRoundData() {

            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val retrofit = Retrofit.Builder()
                .baseUrl("https://192.168.1.9:8080/api/slagalica/")
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

        call.enqueue(object: Callback<AsocijacijeModel> {
            override fun onResponse(call: Call<AsocijacijeModel>, response: Response<AsocijacijeModel>) {
                if(response.isSuccessful){
                    asocijacijeResponse = response.body()

                }
                else {
                    Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AsocijacijeModel>, t: Throwable) {
                Toast.makeText(applicationContext, "API call failed", Toast.LENGTH_SHORT).show()
            }

        })

    }

}