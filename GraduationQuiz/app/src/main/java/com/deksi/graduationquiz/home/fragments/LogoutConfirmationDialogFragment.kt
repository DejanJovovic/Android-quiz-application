package com.deksi.graduationquiz.home.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.home.HomeActivity

class LogoutConfirmationDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    (activity as? HomeActivity)?.logout()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // User clicked No, dismiss the dialog
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}