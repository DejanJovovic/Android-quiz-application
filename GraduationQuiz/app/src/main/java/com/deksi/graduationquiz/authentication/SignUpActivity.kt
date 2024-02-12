package com.deksi.graduationquiz.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()

    }

    private fun setupListeners() {

        binding.buttonGetStarted.setOnClickListener {

            val email = binding.editTextEmailAddressSignup.text.toString()
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPasswordSignup.text.toString()
            val retypePassword = binding.editTextRetypePassword.text.toString()

            if (email.isEmpty()) {
                binding.editTextEmailAddressSignup.error = "Email required"
                binding.editTextEmailAddressSignup.requestFocus()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                binding.editTextUsername.error = "Username required"
                binding.editTextUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.editTextPasswordSignup.error = "Password required"
                binding.editTextPasswordSignup.requestFocus()
                return@setOnClickListener
            }

            //needs fixing
//            if(retypePassword == password) {
//                binding.editTextRetypePassword.error = "Password does not match"
//                binding.editTextRetypePassword.requestFocus()
//                return@setOnClickListener
//            }

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
                .baseUrl("https://192.168.1.9:8080/api/users/")
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
//                        Snackbar.make(
//                            binding.buttonGetStarted,
//                            "User signed up successfully!",
//                            Snackbar.LENGTH_LONG
//                        ).show()
                        val intent = Intent(applicationContext, LogInActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(applicationContext, "Error!", Toast.LENGTH_LONG).show()

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
}