package com.deksi.graduationquiz.password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityForgotPasswordBinding
import com.deksi.graduationquiz.password.api.ChangePasswordApiService
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

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        setupListeners()

    }

    private fun setUpActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun setupListeners() {

        val emailEditText = binding.editTextEmailAddress

        binding.buttonForgotPasswordSubmit.setOnClickListener {

            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                sendEmailForChangePassword(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailForChangePassword(email: String) {

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

        val changePasswordService = retrofit.create(ChangePasswordApiService::class.java)

        val request = mapOf("email" to email)
        val call = changePasswordService.sendEmailForForgotPassword(request)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Email sent successfully, show success message to the user
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Verification code sent successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Proceed to the next activity (ForgotPasswordVerificationActivity)
                    startActivity(
                        Intent(
                            this@ForgotPasswordActivity,
                            ForgotPasswordVerificationActivity::class.java
                        )
                    )
                } else {
                    // Show error message to the user
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Failed to send verification code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle network errors
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
