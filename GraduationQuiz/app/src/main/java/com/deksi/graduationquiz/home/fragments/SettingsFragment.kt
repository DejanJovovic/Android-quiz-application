package com.deksi.graduationquiz.home.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.deksi.graduationquiz.MainActivity
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.authentication.viewModel.UsernameViewModel
import com.deksi.graduationquiz.databinding.FragmentSettingsBinding
import com.deksi.graduationquiz.home.HomeActivity
import com.deksi.graduationquiz.home.adapter.SpinnerArrayAdapter
import java.util.Locale


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var spinner: Spinner
    private var selectedLanguage: String? = null
    private lateinit var usernameViewModel: UsernameViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameViewModel = ViewModelProvider(requireActivity()).get(UsernameViewModel::class.java)


        setOnClickListeners()
        setOnItemSelectedListener()

    }

    private fun setOnClickListeners() {
        binding.buttonSaveChangedLanguage.setOnClickListener {
            showConfirmationDialog()
        }
        binding.buttonAllowTutorials.setOnClickListener {
            val username = getUsername()
            resetTutorialPreferences(username)
            Toast.makeText(requireContext(), "Tutorial preferences reset", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetTutorialPreferences(username: String) {
        Log.d("SettingsFragment", "Resetting tutorial preferences for username: $username")
        val sharedPreferences = requireContext().getSharedPreferences("TutorialPreferences", Context.MODE_PRIVATE)
        val isRemoved = sharedPreferences.edit().remove(username).commit()
        if (isRemoved) {
            Log.d("SettingsFragment", "Tutorial preferences successfully reset for username: $username")
        } else {
            Log.e("SettingsFragment", "Failed to reset tutorial preferences for username: $username")
        }
        val isTutorialShownAfterReset = sharedPreferences.getBoolean(username, false)
        Log.d("SettingsFragment", "Is tutorial shown after reset: $isTutorialShownAfterReset")
    }

    private fun getUsername(): String {
        val username: String = if (HomeActivity.loginType == "regular") {
            usernameViewModel.regularUsername
        } else {
            usernameViewModel.guestUsername
        }
        Log.d("SettingsFragment", "Username obtained: $username")
        return username
    }

    private fun setOnItemSelectedListener() {
        spinner = binding.spinnerLanguage

        val sharedPreferences = requireContext().getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString("selectedLanguage", "en") ?: "en"

        val languagesResourceId = resources.getIdentifier("languages", "array", requireContext().packageName)
        val languages: Array<String> = if (languagesResourceId != 0) {
            resources.getStringArray(languagesResourceId)
        } else {
            resources.getStringArray(R.array.languages)
        }

        val adapter = SpinnerArrayAdapter(requireContext(), languages)
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
            getString(R.string.yes)
        }
        val no = if (noResourceId != 0) {
            getString(noResourceId)
        } else {
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
            getString(R.string.settings_confirmation_message)
        }

        val yesSpannable = SpannableString(yes).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noSpannable = SpannableString(no).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val messageSpannable = SpannableString(message).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val titleSpannable = SpannableString(title).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.textColor)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(titleSpannable)
        builder.setMessage(messageSpannable)
        builder.setPositiveButton(yesSpannable) { _, _ ->
            selectedLanguage?.let { setLanguage(it) }
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            requireActivity().finish()
        }
        builder.setNegativeButton(noSpannable) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


}