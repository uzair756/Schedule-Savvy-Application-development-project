package com.example.mad_cv_sp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity6 : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        database = FirebaseDatabase.getInstance().getReference("students")

        val accountExistTextView = findViewById<TextView>(R.id.account_student_signin_exist)
        val signinTextView = findViewById<TextView>(R.id.student_signin)

        accountExistTextView.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        signinTextView.setOnClickListener {
            performSignin()
        }
    }

    private fun performSignin() {
        val username = findViewById<EditText>(R.id.username_student_signin).text.toString().trim()
        val regNo = findViewById<EditText>(R.id.regno_student_signin).text.toString().trim()
        val password = findViewById<EditText>(R.id.password_student_signin).text.toString().trim()

        if (username.isEmpty() || regNo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (regNo.length != 9) {
            Toast.makeText(this, "Registration number must be 9 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        // You can add additional validation here if needed

        saveUserToDatabase(username, regNo, password)
    }

    private fun saveUserToDatabase(username: String, regNo: String, password: String) {
        val userId = database.push().key
        val user = mapOf(
            "username" to username,
            "regNo" to regNo,
            "password" to password
        )

        if (userId != null) {
            database.child(userId).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User signed in successfully", Toast.LENGTH_SHORT).show()
                    clearInputFields() // Clear input fields after successful sign-in
                    val intent = Intent(this, MainActivity4::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to sign in user", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Failed to generate user ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputFields() {
        val usernameEditText = findViewById<EditText>(R.id.username_student_signin)
        val regNoEditText = findViewById<EditText>(R.id.regno_student_signin)
        val passwordEditText = findViewById<EditText>(R.id.password_student_signin)

        usernameEditText.setText("")
        regNoEditText.setText("")
        passwordEditText.setText("")
    }
}
