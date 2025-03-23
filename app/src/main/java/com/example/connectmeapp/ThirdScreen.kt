package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ThirdScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    companion object {
        private const val TAG = "ThirdScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third_screen)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        nameEditText = findViewById(R.id.name)
        usernameEditText = findViewById(R.id.username_input)
        phoneEditText = findViewById(R.id.phone_input)
        emailEditText = findViewById(R.id.email_input)
        passwordEditText = findViewById(R.id.password_input)
        registerButton = findViewById(R.id.register_button)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginText = findViewById<TextView>(R.id.already_login)
        loginText.setOnClickListener {
            val intent = Intent(this@ThirdScreen, SecondScreen::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, R.anim.slide_in_left, R.anim.slide_out_right
            )
            startActivity(intent, options.toBundle())
            finish()
        }

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = nameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val phoneNumber = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validate input
        if (username.isEmpty()) {
            usernameEditText.error = "Username is required"
            usernameEditText.requestFocus()
            return
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            emailEditText.requestFocus()
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            passwordEditText.requestFocus()
            return
        }

        if (password.length < 6) {
            passwordEditText.error = "Password should be at least 6 characters"
            passwordEditText.requestFocus()
            return
        }

        // Visual feedback without progressBar
        registerButton.isEnabled = false
        registerButton.text = "Registering..."

        Log.d(TAG, "Starting user registration for email: $email")

        // Check network connectivity
        if (!isNetworkAvailable()) {
            registerButton.isEnabled = true
            registerButton.text = "Register"
            Toast.makeText(this, "No internet connection. Please check your connection and try again.",
                Toast.LENGTH_LONG).show()
            return
        }

        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")

                    // Sign up success, save user info to database
                    val user = auth.currentUser
                    val userId = user?.uid ?: return@addOnCompleteListener // UID is now unique


                    if (userId == null) {
                        Log.e(TAG, "User ID is null after successful registration")
                        registerButton.isEnabled = true
                        registerButton.text = "Register"
                        Toast.makeText(baseContext, "Error: Could not retrieve user ID",
                            Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // Save additional user info to Realtime Database - USING SPECIFIC DATABASE URL
                    val database = FirebaseDatabase.getInstance("https://faizan1ahmed1smda2-default-rtdb.asia-southeast1.firebasedatabase.app")
                    val userRef = database.getReference("users").child(userId)

                    // Enable keep synced to ensure data is prioritized for sending
                    userRef.keepSynced(true)

                    val userMap = hashMapOf(
                        "userId" to userId,
                        "name" to name,
                        "username" to username,
                        "phoneNumber" to phoneNumber,
                        "email" to email,
                        "profileImageUrl" to "",
                        "bio" to "",
                        "isNewUser" to true,
                        "createdAt" to System.currentTimeMillis()
                    )

                    Log.d(TAG, "About to write user data to database for UID: $userId")

                    // Add more comprehensive callback listeners
                    userRef.setValue(userMap)
                        .addOnSuccessListener {
                            Log.d(TAG, "User data saved successfully")

                            // Force sync by doing a dummy read operation to confirm data is available
                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    Log.d(TAG, "Data sync confirmed, proceeding with UI updates")

                                    registerButton.isEnabled = true
                                    registerButton.text = "Register"

                                    Toast.makeText(baseContext, "Registration successful!",
                                        Toast.LENGTH_SHORT).show()

                                    // Navigate to EditProfileActivity since it's a new user
                                    val intent = Intent(this@ThirdScreen, EditProfileActivity::class.java)
                                    intent.putExtra("NEW_USER", true)
                                    val options = ActivityOptions.makeCustomAnimation(
                                        this@ThirdScreen, R.anim.slide_in_right, R.anim.slide_out_left
                                    )
                                    startActivity(intent, options.toBundle())
                                    finish()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(TAG, "Data sync check failed", error.toException())

                                    // Even if verification fails, still proceed since data was saved
                                    registerButton.isEnabled = true
                                    registerButton.text = "Register"

                                    Toast.makeText(baseContext, "Registration successful but verification had issues!",
                                        Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@ThirdScreen, EditProfileActivity::class.java)
                                    intent.putExtra("NEW_USER", true)
                                    val options = ActivityOptions.makeCustomAnimation(
                                        this@ThirdScreen, R.anim.slide_in_right, R.anim.slide_out_left
                                    )
                                    startActivity(intent, options.toBundle())
                                    finish()
                                }
                            })
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to save user data", e)
                            registerButton.isEnabled = true
                            registerButton.text = "Register"

                            Toast.makeText(baseContext, "Error saving user data: ${e.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                        .addOnCanceledListener {
                            Log.d(TAG, "setValue operation was canceled")
                            registerButton.isEnabled = true
                            registerButton.text = "Register"

                            Toast.makeText(baseContext, "Data save operation canceled",
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If sign up fails, display a message to the user.
                    Log.e(TAG, "createUserWithEmail:failure", task.exception)
                    registerButton.isEnabled = true
                    registerButton.text = "Register"

                    Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return actNw.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onResume() {
        super.onResume()
        // Reset button state if needed
        registerButton.isEnabled = true
        registerButton.text = "Register"
    }
}