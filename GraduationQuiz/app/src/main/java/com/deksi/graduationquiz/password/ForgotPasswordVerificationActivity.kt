package com.deksi.graduationquiz.password

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Toast
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

        // Retrieve the verification code from intent extras
        verificationCode = intent.getStringExtra("verificationCode") ?: ""
         email = intent.getStringExtra("email") ?: ""

        setOnClickListener()
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
            intent.putExtra("email", email) // Pass the email to ResetPasswordActivity
            startActivity(intent)
            finish()
        } else {
                // Codes don't match
                attemptsRemaining--
                if (attemptsRemaining > 0) {
                    // Still attempts remaining, show remaining attempts message
                    Toast.makeText(
                        this, "Incorrect verification code. $attemptsRemaining attempts remaining.",
                        Toast.LENGTH_SHORT
                    ).show()
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
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Time is up!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun showProgressDialogNoAttemptsRemaining() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("No attempts remaining!")
        progressDialog!!.setCancelable(false)
        progressDialog!!.max = totalTime.toInt()
        progressDialog!!.show()

        startTimerProgressDialog()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun moveBackToProfileWithDelay() {
        val delayMilis = 5000L
        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this@ForgotPasswordVerificationActivity, HomeActivity::class.java)
            startActivity(intent)

            finish()
        }, delayMilis)
    }
}