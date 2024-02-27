package com.deksi.graduationquiz.home.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.databinding.FragmentSettingsBinding
import java.util.Locale


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private var spinner: Spinner? = null
    private val languages = arrayOf("English", "Serbian", "Spanish")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = binding.spinnerLanguage

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter

    
        val sharedPreferences = requireContext().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") // Default to English if no language is saved
        val index = languages.indexOfFirst { it.equals(savedLanguage, ignoreCase = true) }
        spinner!!.setSelection(index)



        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                binding.buttonSaveChangedLanguage.isEnabled = true
                when (parent?.getItemAtPosition(position).toString()) {
                    "English" -> setLanguage("en")
                    "Serbian" -> setLanguage("sr")
                    "Spanish" -> setLanguage("es")

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.buttonSaveChangedLanguage.setOnClickListener {
            showConfirmationDialog()
        }

    }

    private fun setLanguage(languageCode: String) {
        saveLanguagePreference(languageCode)
        setLocale(requireActivity(), languageCode)
    }

    private fun saveLanguagePreference(languageCode: String) {
        val sharedPreferences = requireContext().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("selectedLanguage", languageCode).apply()
    }

    private fun setLocale(activity: Activity, langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val resources: Resources = activity.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)

        activity.baseContext.resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Are you sure?")
        builder.setMessage("Do you want to proceed with the language change?")
        builder.setPositiveButton("Yes") { _, _ ->
            val intent = Intent(requireContext(), LogInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}