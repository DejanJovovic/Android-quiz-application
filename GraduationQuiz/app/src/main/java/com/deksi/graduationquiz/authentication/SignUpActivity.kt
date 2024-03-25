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
import com.deksi.graduationquiz.authentication.api.SignUpService
import com.deksi.graduationquiz.authentication.model.User
import com.deksi.graduationquiz.databinding.ActivitySignUpBinding

import com.deksi.graduationquiz.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
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

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        binding.buttonGetStarted.setOnClickListener {

            val email = binding.editTextEmailAddressSignup.text.toString()
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPasswordSignup.text.toString()
            val retypePassword = binding.editTextRetypePassword.text.toString()
            onSignUpProgressDialog()

            getSavedLanguageBySharedPreferences()

            val emailResourceId = resources.getIdentifier("signup_email_required", "string", packageName)
            val emailRequired = if (emailResourceId != 0) {
                getString(emailResourceId)
            } else {
                getString(R.string.signup_email_required)
            }

            val usernameResourceId = resources.getIdentifier("signup_username_required", "string", packageName)
            val usernameRequired = if (usernameResourceId != 0) {
                getString(usernameResourceId)
            } else {
                getString(R.string.signup_username_required)
            }

            val passwordResourceId = resources.getIdentifier("signup_password_required", "string", packageName)
            val passwordRequired = if (passwordResourceId != 0) {
                getString(passwordResourceId)
            } else {
                getString(R.string.signup_password_required)
            }

            val retypePasswordResourceId = resources.getIdentifier("signup_retype_required", "string", packageName)
            val retypePasswordRequired = if (retypePasswordResourceId != 0) {
                getString(retypePasswordResourceId)
            } else {
                getString(R.string.signup_retype_required)
            }

            val passwordsDontMatchResourceId = resources.getIdentifier("signup_passwords_dont_match", "string", packageName)
            val passwordsDontMatch = if (passwordsDontMatchResourceId != 0) {
                getString(passwordsDontMatchResourceId)
            } else {
                getString(R.string.signup_passwords_dont_match)
            }

            if (email.isEmpty()) {
                binding.editTextEmailAddressSignup.error = emailRequired
                binding.editTextEmailAddressSignup.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.editTextUsername.error = usernameRequired
                binding.editTextUsername.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.editTextPasswordSignup.error = passwordRequired
                binding.editTextPasswordSignup.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }

            if (retypePassword.isEmpty()) {
                binding.editTextRetypePassword.error = retypePasswordRequired
                binding.editTextRetypePassword.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }

            if (password != retypePassword) {
                binding.editTextRetypePassword.error = passwordsDontMatch
                binding.editTextRetypePassword.requestFocus()
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

            val signUpService = retrofit.create(SignUpService::class.java)



            val user = User(email, username, password)
            val call = signUpService.signUp(user)
            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "User signed up successfully!", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, LogInActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        dismissProgressDialog()
                    }
                    else{
                        Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()
                        dismissProgressDialog()

                    }

                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("Error", "Failed to make the API request", t)
                }

            })
        }

        binding.textViewLogInFromSignup.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun onSignUpProgressDialog() {
        getSavedLanguageBySharedPreferences()

        val titleResourceId = resources.getIdentifier("signup_title", "string", packageName)
        val title = if (titleResourceId != 0) {
            getString(titleResourceId)
        } else {
            getString(R.string.signup_title)
        }

        val messageResourceId = resources.getIdentifier("signup_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.signup_message)
        }

        val titleSpannable = SpannableString(title).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@SignUpActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@SignUpActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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