package com.deksi.graduationquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.authentication.SignUpActivity
import com.deksi.graduationquiz.databinding.ActivityMainBinding
import com.deksi.graduationquiz.slagalica.activities.Asosijacije
import com.deksi.graduationquiz.slagalica.activities.KoZnaZna
import com.deksi.graduationquiz.slagalica.activities.KorakPoKorak
import com.deksi.graduationquiz.slagalica.activities.MojBroj
import com.deksi.graduationquiz.slagalica.activities.Skocko
import com.deksi.graduationquiz.slagalica.activities.Spojnice

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.testButton.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

    }
}