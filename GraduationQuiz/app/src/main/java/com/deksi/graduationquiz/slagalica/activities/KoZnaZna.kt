package com.deksi.graduationquiz.slagalica.activities

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityKoZnaZnaBinding
import com.deksi.graduationquiz.slagalica.api.KoZnaZnaApiService
import com.deksi.graduationquiz.slagalica.model.KoZnaZnaModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.LinkedList
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class KoZnaZna : AppCompatActivity() {

    private lateinit var binding: ActivityKoZnaZnaBinding
    private var questions: List<KoZnaZnaModel> = emptyList()
    private var currentQuestionIndex:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKoZnaZnaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        getRoundData()
    }

    private fun displayData() {
        val question = binding.textViewQuestion
        val option1 = binding.buttonOption1
        val option2 = binding.buttonOption2
        val option3 = binding.buttonOption3
        val option4 = binding.buttonOption4

        val currentQuestion = questions[currentQuestionIndex]

        question.text = currentQuestion.question
        option1.text = currentQuestion.option1
        option2.text = currentQuestion.option2
        option3.text = currentQuestion.option3
        option4.text = currentQuestion.option4

        option1.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option1, option1)
        }

        option2.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option2, option2)
        }

        option3.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option3, option3)
        }

        option4.setOnClickListener {
            handleButtonClick(currentQuestion, currentQuestion.option4, option4)
        }
    }

    private fun handleButtonClick(question: KoZnaZnaModel, selectedOption: String, clickedButton: Button) {
        if (selectedOption == question.answer) {
            // Correct answer, update UI (change color to green)
            clickedButton.setBackgroundResource(R.drawable.round_green_reveal)
        } else {
            // Incorrect answer, update UI (change color to red)
            clickedButton.setBackgroundResource(R.drawable.round_red)

            // Display the correct answer (change color to green)
            when (question.answer) {
                question.option1 -> binding.buttonOption1.setBackgroundResource(R.drawable.round_green_reveal)
                question.option2 -> binding.buttonOption2.setBackgroundResource(R.drawable.round_green_reveal)
                question.option3 -> binding.buttonOption3.setBackgroundResource(R.drawable.round_green_reveal)
                question.option4 -> binding.buttonOption4.setBackgroundResource(R.drawable.round_green_reveal)
            }
        }


        binding.buttonNextQuestion.setOnClickListener {

            // Reset the background of all buttons to their default state
            resetButtonBackgrounds()

            // Increment the index to move to the next question
            currentQuestionIndex++

            // Check if the index is within bounds
            if (currentQuestionIndex < questions.size) {
                // If within bounds, get the next question
                val nextQuestion = questions[currentQuestionIndex]

                // Display the next question
                displayData()
            } else {
                Toast.makeText(this@KoZnaZna, "End of questions", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun resetButtonBackgrounds() {
        binding.buttonOption1.setBackgroundResource(R.drawable.round_white)
        binding.buttonOption2.setBackgroundResource(R.drawable.round_white)
        binding.buttonOption3.setBackgroundResource(R.drawable.round_white)
        binding.buttonOption4.setBackgroundResource(R.drawable.round_white)
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
            .baseUrl("https://192.168.1.9:8080/api/koznazna/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val koZnaZnaApiService = retrofit.create(KoZnaZnaApiService::class.java)

        // Make the API call to get random rounds
        val call = koZnaZnaApiService.getRandomRounds()

        call.enqueue(object : Callback<List<KoZnaZnaModel>> {
            override fun onResponse(
                call: Call<List<KoZnaZnaModel>>,
                response: Response<List<KoZnaZnaModel>>
            ) {
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                    displayData()


                } else {
                    Toast.makeText(this@KoZnaZna, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<KoZnaZnaModel>>, t: Throwable) {
                Toast.makeText(this@KoZnaZna, "API call failed", Toast.LENGTH_SHORT).show()
            }
        })

    }
}