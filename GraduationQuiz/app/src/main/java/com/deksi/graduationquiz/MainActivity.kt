package com.deksi.graduationquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.authentication.SignUpActivity
import com.deksi.graduationquiz.databinding.ActivityMainBinding
import com.deksi.graduationquiz.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupListeners()

    }

    private fun setupListeners() {
        binding.buttonMainLogin.setOnClickListener {
            //treba menjati/samo test
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        binding.buttonMainSignup.setOnClickListener {
            //treba menjati/samo test
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.buttonMainPlayAsGuest.setOnClickListener {
            //treba menjati/samo test
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}