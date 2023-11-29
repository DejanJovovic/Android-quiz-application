package com.deksi.graduationquiz.password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonForgotPasswordSubmit.setOnClickListener {
            // samo za test/treba menjati
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}