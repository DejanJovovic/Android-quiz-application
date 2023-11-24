package com.deksi.graduationquiz.slagalica.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivitySkockoBinding

class Skocko : AppCompatActivity() {

    private lateinit var binding: ActivitySkockoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkockoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}