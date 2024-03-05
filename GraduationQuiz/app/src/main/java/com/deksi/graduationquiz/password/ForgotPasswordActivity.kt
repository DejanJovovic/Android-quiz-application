package com.deksi.graduationquiz.password

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
    private var progressDialog: ProgressDialog? = null

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun setupListeners() {

        val emailEditText = binding.editTextEmailAddress

        binding.buttonForgotPasswordSubmit.setOnClickListener {

            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                onSuccessProgressDialog()
                sendEmailForChangePassword(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSuccessProgressDialog() {

        getSavedLanguageBySharedPreferences()

        val titleResourceId = resources.getIdentifier("forgot_password_title", "string", packageName)
        val title = if (titleResourceId != 0) {
            getString(titleResourceId)
        } else {
            getString(R.string.forgot_password_title)
        }

        val messageResourceId = resources.getIdentifier("forgot_password_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.forgot_password_message)
        }

        progressDialog =
            ProgressDialog.show(this, title, message, true, false)

    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
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

        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    val verificationCode = responseData?.get("verificationCode") as? String
                    if (verificationCode != null) {
                        // Email sent successfully, show success message to the user
                        Toast.makeText(
                            this@ForgotPasswordActivity, "Verification code sent successfully", Toast.LENGTH_SHORT
                        ).show()
                        dismissProgressDialog()
                        val intent = Intent(this@ForgotPasswordActivity, ForgotPasswordVerificationActivity::class.java)
                        intent.putExtra("verificationCode", verificationCode)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        // Show error message to the user
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Failed to retrieve verification code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Failed to send verification code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}
