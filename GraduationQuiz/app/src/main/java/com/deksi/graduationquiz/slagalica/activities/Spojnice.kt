package com.deksi.graduationquiz.slagalica.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.R
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

        getRoundData()
        onClickListeners()
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
            moveToNextActivityWithDelay()
        }
    }

    private fun moveToNextActivityWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this@Spojnice, Asocijacije::class.java)
            startActivity(intent)

            finish()
        }, delayMilis)
    }

    private fun changeButtonColorToRed(buttonId: String?) {
        val button = findButtonByIdentifier(buttonId)
        button?.setBackgroundResource(R.drawable.round_red_reveal)
    }

    private fun changeButtonColorToGreen(buttonId: String?) {
        val button = findButtonByIdentifier(buttonId)
        button?.setBackgroundResource(R.drawable.round_green_reveal)
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
            .baseUrl("https://192.168.197.66:8080/api/spojnice/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val spojniceService = retrofit.create(SpojniceApiService::class.java)

        val call = spojniceService.getRandomRound()

        call.enqueue(object: Callback<SpojniceModel> {
            override fun onResponse(call: Call<SpojniceModel>, response: Response<SpojniceModel>) {
                if(response.isSuccessful){
                    spojniceResponse = response.body()
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
                else {
                    Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SpojniceModel>, t: Throwable) {
                Toast.makeText(applicationContext, "API call failed", Toast.LENGTH_SHORT).show()
            }

        })

    }
}