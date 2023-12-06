package com.deksi.graduationquiz.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.api.LoginRequest
import com.deksi.graduationquiz.authentication.api.LoginResponse
import com.deksi.graduationquiz.authentication.api.LoginService
import com.deksi.graduationquiz.databinding.ActivityLogInBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.password.ForgotPasswordActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LogInActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()

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


            if (username.isEmpty()) {
                binding.editTextUsername.error = "Username required"
                binding.editTextUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.editTextPassword.error = "Password required"
                binding.editTextPassword.requestFocus()
                return@setOnClickListener
            }


            val retrofit = Retrofit.Builder()
                .baseUrl("https://192.168.1.2:8080/api/users/")
                .addConverterFactory(GsonConverterFactory.create())
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
                        val token = response.body()?.token

                        if (token != null) {
                            val sharedPrefs = getSharedPreferences("AuthToken", Context.MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            editor.putString("transferObject", token)
                            editor.apply()

                        }
                        Toast.makeText(applicationContext, "Welcome $username!", Toast.LENGTH_LONG).show()

                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(applicationContext, "Wrong username or password!", Toast.LENGTH_LONG).show()

                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("Error", "Failed to make the API request", t)
                }

            })

        }
    }
}