package com.deksi.graduationquiz.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityLogInBinding
import com.deksi.graduationquiz.password.ForgotPasswordActivity

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
    }
}