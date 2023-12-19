package com.deksi.graduationquiz.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.databinding.FragmentHomeBinding
import com.deksi.graduationquiz.slagalica.activities.Asocijacije
import com.deksi.graduationquiz.slagalica.activities.KoZnaZna
import com.deksi.graduationquiz.slagalica.activities.KorakPoKorak
import com.deksi.graduationquiz.slagalica.activities.MojBroj
import com.deksi.graduationquiz.slagalica.activities.Spojnice

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.buttonPlay.setOnClickListener {
            val intent = Intent(context, Spojnice::class.java)
            startActivity(intent)

        }
    }
}