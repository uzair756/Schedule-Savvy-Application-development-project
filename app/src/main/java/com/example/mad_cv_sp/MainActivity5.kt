package com.example.mad_cv_sp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity5 : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        database = FirebaseDatabase.getInstance().getReference("teachers")

        val accountExistTextView = findViewById<TextView>(R.id.account_teacher_signin_exist)
        val signinTextView = findViewById<TextView>(R.id.teacher_signin)

        accountExistTextView.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        signinTextView.setOnClickListener {
            performSignin()
        }
    }

    private fun performSignin() {
        val username = findViewById<EditText>(R.id.username_teacher_signin).text.toString().trim()
        val email = findViewById<EditText>(R.id.email_teacher_signin).text.toString().trim()
        val teacherCode = findViewById<EditText>(R.id.code_teacher_signin).text.toString().trim()
        val password = findViewById<EditText>(R.id.password_teacher_signin).text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || teacherCode.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (teacherCode != "61") {
            Toast.makeText(this, "Invalid teacher code", Toast.LENGTH_SHORT).show()
            return
        }

        checkEmailExists(email) { exists ->
            if (exists) {
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
            } else {
                saveUserToDatabase(username, email, teacherCode, password)
            }
        }
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity5, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserToDatabase(username: String, email: String, teacherCode: String, password: String) {
        val userId = database.push().key
        val user = mapOf(
            "username" to username,
            "email" to email,
            "teacherCode" to teacherCode,
            "password" to password
        )

        if (userId != null) {
            database.child(userId).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User signed in successfully", Toast.LENGTH_SHORT).show()
                    clearInputFields() // Clear input fields after successful sign-in
                    val intent = Intent(this, MainActivity3::class.java)
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
        val usernameEditText = findViewById<EditText>(R.id.username_teacher_signin)
        val emailEditText = findViewById<EditText>(R.id.email_teacher_signin)
        val codeEditText = findViewById<EditText>(R.id.code_teacher_signin)
        val passwordEditText = findViewById<EditText>(R.id.password_teacher_signin)

        usernameEditText.setText("")
        emailEditText.setText("")
        codeEditText.setText("")
        passwordEditText.setText("")
    }
}
