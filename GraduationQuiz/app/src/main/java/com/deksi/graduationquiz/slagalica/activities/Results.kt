package com.deksi.graduationquiz.slagalica.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.api.UserScoreService
import com.deksi.graduationquiz.authentication.model.UserScore
import com.deksi.graduationquiz.databinding.ActivityResultsBinding
import com.deksi.graduationquiz.home.HomeActivity
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

class Results : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private var progressDialog: ProgressDialog? = null
    private val totalTime: Long = 5000
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickListeners()
        showTotalScore()
        saveUsernameAndTotalPoints()
    }

    private fun saveUsernameAndTotalPoints() {

        val usernameSharedPreferences = getSharedPreferences("UsernamePref", Context.MODE_PRIVATE)
        val username = usernameSharedPreferences.getString("username", "")

        val totalPointsSharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val totalScore = totalPointsSharedPreferences.getInt("totalScore", 0)

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
            .baseUrl("https://192.168.1.9:8080/api/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val userScoreService = retrofit.create(UserScoreService::class.java)
        val userScore = UserScore(username!!, totalScore)

        val call = userScoreService.updateScore(userScore)

        call?.enqueue(object: Callback<Void?>{
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if(response.isSuccessful){

                    Log.d("UpdateScore", "Username: $username, Total Score: $totalScore")
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

    private fun onClickListeners() {
        binding.buttonBackToHome.setOnClickListener {
            showProgressDialogOnNextRound()
            clearTotalScoreFromPreferences() // clears the current totalScore value from local memory
        }
    }

    private fun showTotalScore() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)

        // Retrieve the overall total score
        val totalScore = sharedPreferences.getInt("totalScore", 0)

        val messageResourceId = resources.getIdentifier("timer_total_score", "string", packageName)
        val messageScore = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.timer_total_score)
        }

        // Now, 'totalScore' contains the sum of scores across all games
        binding.textViewTotalScore.text = "$messageScore: $totalScore"
    }

    private fun clearTotalScoreFromPreferences() {
        val sharedPreferences = getSharedPreferences("GameScores", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the totalScore key from SharedPreferences
        editor.remove("totalScore")
        editor.apply()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showProgressDialogOnNextRound() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("results_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.results_message)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun startTimerProgressDialog() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (totalTime - millisUntilFinished).toInt()

                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining"
                progressDialog?.setMessage(message)

            }

            override fun onFinish() {
                dismissProgressDialog()
                finish()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    override fun onDestroy() {
        super.onDestroy()
        clearTotalScoreFromPreferences()
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
                clearTotalScoreFromPreferences()
            }
            .setNegativeButton(no) { _, _ ->
                // Do nothing
            }
            .show()

    }
}