package com.deksi.graduationquiz

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.authentication.SignUpActivity
import com.deksi.graduationquiz.databinding.ActivityMainBinding
import com.deksi.graduationquiz.home.HomeActivity
import java.util.Locale
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSelectedLanguage()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupListeners()
    }

    private fun setSelectedLanguage() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en")

        setLocale(this, savedLanguage!!)
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
            randomizeUsernameAndMoveToHome()
        }
    }

    private fun randomizeUsernameAndMoveToHome() {
        val randomUsername = generateRandomUsername()

        // Save the random username to SharedPreferences
        val sharedPrefs = getSharedPreferences("GuestPreferences", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("guestUsername", randomUsername)
        editor.apply()

        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("loginType", "guest")
        startActivity(intent)
        Toast.makeText(this@MainActivity, "Welcome $randomUsername", Toast.LENGTH_SHORT).show()
    }


    private fun generateRandomUsername(): String {
        val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val length = 8
        val random = Random()

        return (1..length)
            .map { allowedChars[random.nextInt(allowedChars.length)] }
            .joinToString("")
    }

    private fun setLocale(context: Context, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)
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

        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(yes) { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton(no) { _, _ ->
                // Do nothing
            }
            .show()

    }
}