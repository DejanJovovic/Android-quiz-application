package com.deksi.graduationquiz.password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        email = intent.getStringExtra("email") ?: ""

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.buttonResetPasswordSubmit.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val newPassword = binding.editTextNewPassword.text.toString()
        val retypedPassword = binding.editTextRetypeNewPassword.text.toString()

        if (newPassword.isEmpty() || retypedPassword.isEmpty()) {
            Toast.makeText(this, "Please enter both passwords", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != retypedPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        updatePasswordInDatabase(email, newPassword)
    }

    private fun updatePasswordInDatabase(email: String, newPassword: String) {

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

        val updatePasswordService = retrofit.create(UpdatePasswordApiService::class.java)
        val call = updatePasswordService.updatePassword(email, newPassword)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ResetPasswordActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {

                    Toast.makeText(this@ResetPasswordActivity, "Failed to update password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })

    }
}