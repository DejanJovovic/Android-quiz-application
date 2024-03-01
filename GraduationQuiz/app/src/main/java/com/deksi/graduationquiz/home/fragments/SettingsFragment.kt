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
    private lateinit var spinner: Spinner
    private var selectedLanguage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
        setOnItemSelectedListener()

    }

    private fun setOnItemSelectedListener() {
        spinner = binding.spinnerLanguage

        val sharedPreferences = requireContext().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"

        val languagesResourceId = resources.getIdentifier("languages", "array", requireContext().packageName)
        val languages: Array<String> = if (languagesResourceId != 0) {
            resources.getStringArray(languagesResourceId)
        } else {
            // Fallback to default difficulty options if translation is not found
            resources.getStringArray(R.array.languages)
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val index = languages.indexOfFirst { it.equals(savedLanguage, ignoreCase = true) }
        spinner.setSelection(index)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                binding.buttonSaveChangedLanguage.isEnabled = true
                selectedLanguage = when (parent?.getItemAtPosition(position).toString()) {
                    resources.getStringArray(R.array.languages)[0] -> "en"
                    resources.getStringArray(R.array.languages)[1] -> "sr"
                    resources.getStringArray(R.array.languages)[2] -> "es"
                    else -> null
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun setOnClickListeners() {
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

        val sharedPreferences = requireContext().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"

        val yesResourceId = resources.getIdentifier("yes", "string", requireContext().packageName)
        val noResourceId = resources.getIdentifier("no", "string", requireContext().packageName)
        val yes = if (yesResourceId != 0) {
            getString(yesResourceId)
        } else {
            // Fallback to English if translation is not found
            getString(R.string.yes)
        }
        val no = if (noResourceId != 0) {
            getString(noResourceId)
        } else {
            // Fallback to English if translation is not found
            getString(R.string.no)
        }

        val titleId = resources.getIdentifier("setting_confirmation", "string", requireContext().packageName)
        val title = if (titleId != 0) {
            getString(titleId)
        } else {
            // Fallback to English if translation is not found
            getString(R.string.settings_confirmation)
        }

        val messageId = resources.getIdentifier("setting_confirmation_message", "string", requireContext().packageName)
        val message = if (messageId != 0) {
            getString(messageId)
        } else {
            // Fallback to English if translation is not found
            getString(R.string.settings_confirmation_message)
        }



        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(yes) { _, _ ->
            selectedLanguage?.let { setLanguage(it) }
            val intent = Intent(requireContext(), LogInActivity::class.java)
            startActivity(intent)

            requireActivity().finish()
        }
        builder.setNegativeButton(no) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}