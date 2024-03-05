package com.deksi.graduationquiz.home.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.deksi.graduationquiz.databinding.FragmentProfileBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.password.ForgotPasswordActivity
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


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showUserData()
        setUpClickListeners()

    }

    private fun showUserData() {
        val displayUsername = binding.editTextUsername
        val displayPassword = binding.editTextPassword
        val displayEmail = binding.editTextEmail

        val loginType = HomeActivity.loginType
        if (loginType == "regular") {
            (activity as? HomeActivity)?.apply {
                displayUsername.setText(username)
                displayPassword.setText(password)
                displayEmail.setText(email)
            }
        } else if (loginType == "guest") {
            val sharedPrefs =
                requireActivity().getSharedPreferences("GuestPreferences", Context.MODE_PRIVATE)
            val guestUsername = sharedPrefs.getString("guestUsername", "")
            displayUsername.setText(guestUsername)
            // password and email are blank for the guest user
            displayPassword.text = null
            displayEmail.text = null
        }
    }

    private fun setUpClickListeners() {
        binding.buttonChangeCredentials.setOnClickListener {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

}