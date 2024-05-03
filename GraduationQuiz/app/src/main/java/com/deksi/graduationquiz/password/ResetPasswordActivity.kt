package com.deksi.graduationquiz.password

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityResetPasswordBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.home.fragments.ProfileFragment
import com.deksi.graduationquiz.password.api.UpdatePasswordApiService
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

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var email: String
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        getEmail()
        resetPassword()
    }

    private fun getEmail() {
        email = intent.getStringExtra("email") ?: ""
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

    private fun resetPassword() {

        binding.buttonResetPasswordSubmit.setOnClickListener {
            val newPassword = binding.editTextNewPassword.text.toString()
            val retypedPassword = binding.editTextRetypeNewPassword.text.toString()

            getSavedLanguageBySharedPreferences()
            val passwordResourceId = resources.getIdentifier("reset_password_required", "string", packageName)
            val newPasswordRequired = if (passwordResourceId != 0) {
                getString(passwordResourceId)
            } else {
                getString(R.string.reset_password_required)
            }

            val retypePasswordResourceId = resources.getIdentifier("reset_retype_required", "string", packageName)
            val retypeNewPasswordRequired = if (retypePasswordResourceId != 0) {
                getString(retypePasswordResourceId)
            } else {
                getString(R.string.reset_retype_required)
            }

            val passwordsDontMatchResourceId = resources.getIdentifier("reset_passwords_dont_match", "string", packageName)
            val passwordsDontMatch = if (passwordsDontMatchResourceId != 0) {
                getString(passwordsDontMatchResourceId)
            } else {
                getString(R.string.reset_passwords_dont_match)
            }


            if (newPassword.isEmpty()) {
                binding.editTextNewPassword.error = newPasswordRequired
                binding.editTextNewPassword.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }

            if (retypedPassword.isEmpty()) {
                binding.editTextRetypeNewPassword.error = retypeNewPasswordRequired
                binding.editTextRetypeNewPassword.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }

            if (newPassword != retypedPassword) {
                binding.editTextRetypeNewPassword.error = passwordsDontMatch
                binding.editTextRetypeNewPassword.requestFocus()
                dismissProgressDialog()
                return@setOnClickListener
            }

            onResetPasswordProgressDialog()
            updatePasswordInDatabase(email, newPassword)
        }
    }

    private fun updatePasswordInDatabase(email: String, newPassword: String) {

        val sharedPrefs = getSharedPreferences("AuthToken", Context.MODE_PRIVATE)
        val token = sharedPrefs.getString("transferObject", null)

        if (token != null) {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    }

                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
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
                .baseUrl("https://192.168.1.10:8080/api/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                        .hostnameVerifier { _, _ -> true }
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer $token")
                                .build()
                            chain.proceed(request)
                        }
                        .build()
                )
                .build()

            val updatePasswordService = retrofit.create(UpdatePasswordApiService::class.java)
            val call = updatePasswordService.updatePassword(email, newPassword)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ResetPasswordActivity, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                        dismissProgressDialog()
                    } else {
                        Toast.makeText(this@ResetPasswordActivity, "Failed to update password", Toast.LENGTH_SHORT).show()
                        dismissProgressDialog()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@ResetPasswordActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            dismissProgressDialog()
        }
    }

    private fun onResetPasswordProgressDialog() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("reset_password_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.reset_password_message)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ResetPasswordActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun onBackPressed() {

        val messageResourceId = resources.getIdentifier("on_back_pressed_message", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.on_back_pressed_message)
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

        val yesSpannable = SpannableString(yes).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ResetPasswordActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noSpannable = SpannableString(no).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ResetPasswordActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ResetPasswordActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        AlertDialog.Builder(this)
            .setMessage(messageSpannable)
            .setPositiveButton(yesSpannable) { _, _ ->
                super.onBackPressed()
                finish()
            }
            .setNegativeButton(noSpannable) { _, _ ->
                // Do nothing
            }
            .show()

    }
}