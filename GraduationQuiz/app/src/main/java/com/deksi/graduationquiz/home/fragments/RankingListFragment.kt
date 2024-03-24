package com.deksi.graduationquiz.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deksi.graduationquiz.authentication.adapters.UserScoreAdapter
import com.deksi.graduationquiz.authentication.api.GetUserScoreService
import com.deksi.graduationquiz.authentication.model.UserScore
import com.deksi.graduationquiz.authentication.viewModel.UsernameViewModel
import com.deksi.graduationquiz.databinding.FragmentRankingListBinding
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


class RankingListFragment : Fragment() {

    private lateinit var binding: FragmentRankingListBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankingListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserScores()
    }


    private fun fetchUserScores() {
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val retrofit = Retrofit.Builder()
            .baseUrl("https://192.168.1.9:8080/api/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    .build()
            )
            .build()

        val getUserScoresService = retrofit.create(GetUserScoreService::class.java)
        val call = getUserScoresService.getUserScores()

        call.enqueue(object : Callback<List<UserScore>> {
            override fun onResponse(call: Call<List<UserScore>>, response: Response<List<UserScore>>) {
                if (response.isSuccessful) {
                    val userScores = response.body()
                    // Process user scores
                    userScores?.let {
                        // Display user scores in UI (Step 4)
                        showUserScores(it)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Log.e("API Error", "Failed to fetch user scores: $errorMessage")                }
            }

            override fun onFailure(call: Call<List<UserScore>>, t: Throwable) {
                Toast.makeText(context, "Failed to fetch user scores: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to display user scores in UI
    private fun showUserScores(userScores: List<UserScore>) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UserScoreAdapter(userScores)
        recyclerView.adapter = adapter
    }


}