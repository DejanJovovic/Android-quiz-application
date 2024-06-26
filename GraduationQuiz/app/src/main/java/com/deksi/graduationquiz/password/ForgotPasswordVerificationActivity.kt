package com.deksi.graduationquiz.password

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.ActivityForgotPasswordVerificationBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.home.fragments.ProfileFragment
import com.deksi.graduationquiz.slagalica.activities.Spojnice

class ForgotPasswordVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordVerificationBinding
    private var timeLeft: CountDownTimer? = null
    private var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private val totalTime: Long = 5000
    private lateinit var verificationCode: String
    private lateinit var email: String
    private var attemptsRemaining = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTimer()
        setUpActionBar()
        getVerificationCode()
        setOnClickListener()
    }

    private fun getVerificationCode() {
        verificationCode = intent.getStringExtra("verificationCode") ?: ""
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

    private fun setOnClickListener() {
        binding.buttonSubmit.setOnClickListener {
            checkCode()
        }
    }

    private fun checkCode() {
        val enteredCode = binding.editTextVerificationCode.text.toString()
        if (enteredCode == verificationCode) {
            // Codes match, proceed with password reset
            stopTimer()
            val intent = Intent(this@ForgotPasswordVerificationActivity, ResetPasswordActivity::class.java)
            intent.putExtras(
                Bundle().apply {
                    putString("email", email)
                    putString("verificationCode", enteredCode)
                }
            )
            startActivity(intent)
            finish()
        } else {
                // Codes don't match
                attemptsRemaining--
                if (attemptsRemaining > 0) {
                    // Still attempts remaining, show remaining attempts message
                    val verificationCodeId = resources.getIdentifier("verification_code_incorrect", "string", packageName)
                    val verificationCodeRequired = if (verificationCodeId != 0) {
                        getString(verificationCodeId, attemptsRemaining)
                    } else {
                        getString(R.string.verification_code_incorrect)
                    }


                    binding.editTextVerificationCode.error = verificationCodeRequired
                    binding.editTextVerificationCode.requestFocus()

                }
                else {
                    stopTimer()
                    showProgressDialogNoAttemptsRemaining()
                    moveBackToProfileWithDelay()
                }
        }
    }
    
    private fun initTimer() {
        val timerText = binding.textViewTimeLeft
        timeLeft = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                showProgressDialogOnTimeout()
                moveBackToProfileWithDelay()

            }
        }
        (timeLeft as CountDownTimer).start()
    }

    private fun startTimerProgressDialog() {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progressDialog?.progress = (totalTime - millisUntilFinished).toInt()

                val secondsRemaining = millisUntilFinished / 1000
                val message = "$secondsRemaining"
                progressDialog?.setMessage(message)
            }

            override fun onFinish() {
                dismissProgressDialog()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun stopTimer() {
        timeLeft?.cancel()
    }

    private fun showProgressDialogOnTimeout() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("forgot_password_verification_message_on_timeout", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.forgot_password_verification_message_on_timeout)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ForgotPasswordVerificationActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogNoAttemptsRemaining() {
        getSavedLanguageBySharedPreferences()

        val messageResourceId = resources.getIdentifier("forgot_password_verification_message_no_attempts_left", "string", packageName)
        val message = if (messageResourceId != 0) {
            getString(messageResourceId)
        } else {
            getString(R.string.forgot_password_verification_message_no_attempts_left)
        }

        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ForgotPasswordVerificationActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle(messageSpannable)
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun getSavedLanguageBySharedPreferences() {
        val sharedPreferences = getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun moveBackToProfileWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            finish()
        }, delayMilis)
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
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ForgotPasswordVerificationActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noSpannable = SpannableString(no).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ForgotPasswordVerificationActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(this@ForgotPasswordVerificationActivity, R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}