package com.deksi.graduationquiz.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.databinding.FragmentHomeBinding
import com.deksi.graduationquiz.slagalica.activities.KoZnaZna
import com.deksi.graduationquiz.sudoku.Sudoku

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
        binding.buttonSlagalica.setOnClickListener {
            val intent = Intent(context, KoZnaZna::class.java)
            startActivity(intent)

        }
        binding.buttonSudoku.setOnClickListener {
            val intent = Intent(context, Sudoku::class.java)
            startActivity(intent)
        }
    }
}