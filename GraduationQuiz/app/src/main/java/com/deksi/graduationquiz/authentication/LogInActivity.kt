package com.deksi.graduationquiz.authentication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.api.LoginRequest
import com.deksi.graduationquiz.authentication.api.LoginResponse
import com.deksi.graduationquiz.authentication.api.LoginService
import com.deksi.graduationquiz.databinding.ActivityLogInBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.password.ForgotPasswordActivity
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

class LogInActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLogInBinding
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
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
        binding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }


        binding.buttonLogin.setOnClickListener {

            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            onLoginProgressDialog()

            getSavedLanguageBySharedPreferences()

            val usernameResourceId = resources.getIdentifier("login_username_required", "string", packageName)
            val usernameRequired = if (usernameResourceId != 0) {
                getString(usernameResourceId)
            } else {
                getString(R.string.login_username_required)
            }

            val passwordResourceId = resources.getIdentifier("login_password_required", "string", packageName)
            val passwordRequired = if (passwordResourceId != 0) {
                getString(passwordResourceId)
            } else {
                getString(R.string.login_password_required)
            }


            if (username.isEmpty()) {
                binding.editTextUsername.error = usernameRequired
                binding.editTextUsername.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.editTextPassword.error = passwordRequired
                binding.editTextPassword.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }


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
                .baseUrl("https://192.168.31.66:8080/api/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                        .hostnameVerifier { _, _ -> true }
                        .build()
                )
                .build()


            val loginService = retrofit.create(LoginService::class.java)

            val loginRequest = LoginRequest(username, password)

            val call = loginService.login(loginRequest)
            call.enqueue(object: Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        val token = loginResponse?.token
                        val userEmail = loginResponse?.email

                        if (token != null) {
                            val sharedPrefs = getSharedPreferences("AuthToken", Context.MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            editor.putString("transferObject", token)
                            editor.apply()

                        }

                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.putExtra("loginType", "regular")
                        intent.putExtra("username", username)
                        intent.putExtra("password", password)
                        intent.putExtra("email", userEmail)
                        startActivity(intent)
                        dismissProgressDialog()
                    }
                    else{
                        Toast.makeText(applicationContext, "Wrong username or password!", Toast.LENGTH_LONG).show()
                        dismissProgressDialog()

                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Error", "Failed to make the API request", t)
                }

            })

        }
    }

    private fun onLoginProgressDialog() {
        getSavedLanguageBySharedPreferences()

        val titleResourceId = resources.getIdentifier("login_title", "string", packageName)
        val title = if (titleResourceId != 0) {
            getString(titleResourceId)
        } else {
            getString(R.string.login_title)
        }

        val messageResourceId = resources.getIdentifier("login_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.login_message)
        }

        val titleSpannable = SpannableString(title).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@LogInActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@LogInActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog =
            ProgressDialog.show(this, titleSpannable, messageSpannable, true, false)
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

}