package com.deksi.graduationquiz.home.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.home.HomeActivity

class LogoutConfirmationDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

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

            val messageId = resources.getIdentifier("logout_confirmation", "string", requireContext().packageName)
            val message = if (messageId != 0) {
                getString(messageId)
            } else {
                // Fallback to English if translation is not found
                getString(R.string.logout_confirmation)
            }

            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setPositiveButton(yes) { _, _ ->
                    (activity as? HomeActivity)?.logout()
                }
                .setNegativeButton(no) { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}