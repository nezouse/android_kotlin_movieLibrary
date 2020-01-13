package com.movielibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val RC_SIGN_IN = 10
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        navigation.setNavigationItemSelectedListener(this)
        FirebaseAuth.getInstance().currentUser?.let {
            navigation.menu.findItem(R.id.login).title = "Logout"
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.login -> {
                when (item.title) {
                    "Login" -> {
                        logIn()
                    }
                    "Logout" -> {
                        logOut()
                    }
                }
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                navigation.menu.findItem(R.id.login).title = "Logout"
            } else {
                Toast.makeText(this, "There was an error while signing in", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun logIn() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun logOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show()
                navigation.menu.findItem(R.id.login).title = "Login"
            }
    }
}
