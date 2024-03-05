package com.deksi.graduationquiz.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.deksi.graduationquiz.R
import com.deksi.graduationquiz.authentication.LogInActivity
import com.deksi.graduationquiz.databinding.ActivityHomeBinding
import com.deksi.graduationquiz.home.fragments.LogoutConfirmationDialogFragment
import com.deksi.graduationquiz.home.fragments.ProfileFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)
        displayUsername()
        appBarSetup()

//        binding.appBarHome.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

    }

    //i need to check on companion object. What is it etc..
    companion object {
        var loginType: String? = null
    }

    private fun appBarSetup() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun displayUsername() {
        val loginType = intent.getStringExtra("loginType")
        HomeActivity.loginType = loginType
        if (loginType == "regular") {
            displayUsernameOnNavHome()
        } else if (loginType == "guest") {
            displayGuestUsernameOnNavHome()
        }
    }

    private fun displayUsernameOnNavHome(){
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val navHome = navView.getHeaderView(0)
        val displayUsername = navHome.findViewById<TextView>(R.id.text_view_displayed_username)
        username = intent.getStringExtra("username").toString()
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        displayUsername.text = username

    }

    private fun displayGuestUsernameOnNavHome(){
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val navHome = navView.getHeaderView(0)
        val displayUsername = navHome.findViewById<TextView>(R.id.text_view_displayed_username)
        val sharedPrefs = getSharedPreferences("GuestPreferences", Context.MODE_PRIVATE)
        val guestUsername = sharedPrefs.getString("guestUsername", "")
        displayUsername.text = guestUsername

    }

    fun logout() {
        // Removing token from the sharedPreferences when the user logs out
        val sharedPrefs = getSharedPreferences("AuthToken", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.remove("transferObject")
        editor.apply()
//        Log.d("TokenAfterLogout", "Token has been destroyed")

        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

}