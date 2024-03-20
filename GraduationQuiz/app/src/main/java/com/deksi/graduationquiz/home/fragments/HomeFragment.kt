package com.deksi.graduationquiz.home.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.viewModel.UsernameViewModel
import com.deksi.graduationquiz.databinding.FragmentHomeBinding
import com.deksi.graduationquiz.home.HomeActivity

import com.deksi.graduationquiz.slagalica.activities.KoZnaZna
import com.deksi.graduationquiz.sudoku.Sudoku
import com.deksi.graduationquiz.webSocket.GameStateMessage
import com.deksi.graduationquiz.webSocket.WebSocketManager
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var spinner: Spinner
    private lateinit var usernameViewModel: UsernameViewModel
    private lateinit var webSocketManager: WebSocketManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameViewModel = ViewModelProvider(requireActivity()).get(UsernameViewModel::class.java)

        webSocketConnection()
        setUpListeners()
    }

    private fun webSocketConnection() {
        webSocketManager = WebSocketManager(object : WebSocketManager.WebSocketEventListener {
            override fun onConnectionOpened() {
                // Handle WebSocket connection opened
            }

            override fun onGameStateReceived(message: GameStateMessage) {
                activity?.runOnUiThread {
                    // Update UI with received message
                    Toast.makeText(context, "Received message: ${message.gameState} ", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onConnectionClosed() {
                // Handle WebSocket connection closed
            }

            override fun onConnectionFailure(error: String) {
                Log.e("WebSocketManager", "WebSocket connection failure: $error")
            }
        })

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { hostname, session -> true } // Allow all hostnames
            .build()

        webSocketManager.connect(okHttpClient,"https://192.168.228.66:8080/websocket")
    }

    private fun setUpListeners() {
        binding.buttonSlagalica.setOnClickListener {
            val intent = Intent(context, KoZnaZna::class.java)
            val username: String = if (HomeActivity.loginType == "regular") {
                usernameViewModel.regularUsername
            } else {
                usernameViewModel.guestUsername
            }
            intent.putExtra("username", username)
            startActivity(intent)
        }
        binding.buttonSudoku.setOnClickListener {
            showDifficultyDialog()
        }
        binding.buttonSendMessage.setOnClickListener {
            // Send a message when the button is clicked
            val message = "Hello, WebSocket!"
            webSocketManager.sendMessage(message)
        }
    }

    private fun showDifficultyDialog() {

        val sharedPreferences = requireContext().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"

        val positiveResourceId = resources.getIdentifier("positive_button_text", "string", requireContext().packageName)
        val negativeResourceId = resources.getIdentifier("negative_button_text", "string", requireContext().packageName)
        val positive = if (positiveResourceId != 0) {
            getString(positiveResourceId)
        } else {
            getString(R.string.positive_button_text)
        }
        val negative = if (negativeResourceId != 0) {
            getString(negativeResourceId)
        } else {
            getString(R.string.negative_button_text)
        }

        val messageId = resources.getIdentifier("dialog_title_difficulty", "string", requireContext().packageName)
        val message = if (messageId != 0) {
            getString(messageId)
        } else {
            getString(R.string.dialog_title_difficulty)
        }

        val difficultyOptionsResourceId = resources.getIdentifier("difficulty_options", "array", requireContext().packageName)
        val difficultyOptions: Array<String> = if (difficultyOptionsResourceId != 0) {
            resources.getStringArray(difficultyOptionsResourceId)
        } else {
            resources.getStringArray(R.array.difficulty_options)
        }


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(message)
            .setView(R.layout.dialog_difficulty)
            .setPositiveButton(positive) { _, _ ->
                // Handle the selected difficulty
                val selectedDifficulty = spinner.selectedItem as String
                launchSudokuActivity(selectedDifficulty)
            }
            .setNegativeButton(negative, null)
            .create()

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_difficulty, null)
        spinner = dialogView.findViewById(R.id.spinner_difficulty)


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficultyOptions)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        dialog.setView(dialogView)
        dialog.show()
    }

    private fun launchSudokuActivity(difficulty: String) {
        val intent = Intent(context, Sudoku::class.java)
        intent.putExtra("difficulty", difficulty)
        startActivity(intent)
    }
}