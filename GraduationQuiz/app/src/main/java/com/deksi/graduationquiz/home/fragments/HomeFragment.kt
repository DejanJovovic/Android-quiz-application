package com.deksi.graduationquiz.home.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.databinding.FragmentHomeBinding

import com.deksi.graduationquiz.slagalica.activities.KoZnaZna
import com.deksi.graduationquiz.sudoku.Sudoku

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var spinner: Spinner

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

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.buttonSlagalica.setOnClickListener {
            val intent = Intent(context, KoZnaZna::class.java)
            startActivity(intent)

        }
        binding.buttonSudoku.setOnClickListener {
            showDifficultyDialog()
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