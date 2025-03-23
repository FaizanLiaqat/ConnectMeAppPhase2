package com.example.connectmeapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.enableEdgeToEdge

class SecondScreen : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_screen)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById<EditText>(R.id.email_input)
        passwordEditText = findViewById<EditText>(R.id.password_input)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerText = findViewById<TextView>(R.id.register)
        registerText.setOnClickListener {
            val intent = Intent(this@SecondScreen, ThirdScreen::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this, R.anim.slide_in_right, R.anim.slide_out_left
            )
            startActivity(intent, options.toBundle())
            finish()
        }

        val loginBtn = findViewById<Button>(R.id.login_button)
        loginBtn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validate input
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

        // Show progress - you may want to add a ProgressBar for this
        // progressBar.visibility = View.VISIBLE

        // Authenticate with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Hide progress
                // progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SecondScreen, MainActivity::class.java)
                    val options = ActivityOptions.makeCustomAnimation(
                        this, R.anim.slide_in_right, R.anim.slide_out_left
                    )
                    startActivity(intent, options.toBundle())
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}