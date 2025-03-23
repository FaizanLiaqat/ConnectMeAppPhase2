package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val SPLASH_TIMEOUT: Long = 5000 // 5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
//        // For testing purposes, sign out any cached user
        auth.signOut()
        // Use a Handler to delay the next screen
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserLoggedIn()
        }, SPLASH_TIMEOUT)
    }

    private fun checkUserLoggedIn() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is already logged in, check if profile is set up
            val database = FirebaseDatabase.getInstance("https://faizan1ahmed1smda2-default-rtdb.asia-southeast1.firebasedatabase.app")
            val userRef = database.getReference("users").child(currentUser.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val isNewUser = snapshot.child("isNewUser").getValue(Boolean::class.java) ?: true

                        if (isNewUser) {
                            // New user, go to profile setup
                            val intent = Intent(this@SplashScreenActivity, EditProfileActivity::class.java)
                            intent.putExtra("NEW_USER", true)
                            startActivity(intent)
                            finish()
                        } else {
                            // Existing user with profile, go to main activity
                            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // User record doesn't exist, create one
                        val userMap = hashMapOf(
                            "username" to (currentUser.displayName ?: "User"),
                            "email" to (currentUser.email ?: ""),
                            "profileImageUrl" to "",
                            "bio" to "",
                            "isNewUser" to true,
                            "createdAt" to System.currentTimeMillis()
                        )

                        userRef.setValue(userMap).addOnCompleteListener { task ->
                            val intent = Intent(this@SplashScreenActivity, EditProfileActivity::class.java)
                            intent.putExtra("NEW_USER", true)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Error occurred, go to login screen
                    val intent = Intent(this@SplashScreenActivity, SecondScreen::class.java)
                    startActivity(intent)
                    finish()
                }
            })
        } else {
            // No user logged in, go to login screen
            val intent = Intent(this@SplashScreenActivity, SecondScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
}