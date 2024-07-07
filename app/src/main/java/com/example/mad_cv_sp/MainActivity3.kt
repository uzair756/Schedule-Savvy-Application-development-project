package com.example.mad_cv_sp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity3 : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        database = FirebaseDatabase.getInstance().getReference("teachers")

        val accountExistTextView = findViewById<TextView>(R.id.account_teacher_login_exist)
        val loginTextView = findViewById<TextView>(R.id.teacher_login)

        accountExistTextView.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }

        loginTextView.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = findViewById<EditText>(R.id.email_teacher_login).text.toString().trim()
        val password = findViewById<EditText>(R.id.password_teacher_login).text.toString().trim()
        val teacherCode = findViewById<EditText>(R.id.code_teacher_login).text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || teacherCode.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        checkCredentials(email, password, teacherCode)
    }

    private fun checkCredentials(email: String, password: String, teacherCode: String) {
        database.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (teacherSnapshot in dataSnapshot.children) {
                        val storedPassword = teacherSnapshot.child("password").getValue(String::class.java)
                        val storedTeacherCode = teacherSnapshot.child("teacherCode").getValue(String::class.java)
                        val userName = teacherSnapshot.child("username").getValue(String::class.java) ?: "Unknown" // Retrieve user's name

                        if (storedPassword == password && storedTeacherCode == teacherCode) {
                            Toast.makeText(this@MainActivity3, "User logged in successfully", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                            val intent = Intent(this@MainActivity3, MainActivity7::class.java).apply {
                                putExtra("userName", userName) // Pass the user's name to MainActivity7
                            }
                            startActivity(intent)
                            return
                        } else {
                            Toast.makeText(this@MainActivity3, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity3, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity3, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearInputFields() {
        val emailEditText = findViewById<EditText>(R.id.email_teacher_login)
        val passwordEditText = findViewById<EditText>(R.id.password_teacher_login)
        val teacherCodeEditText = findViewById<EditText>(R.id.code_teacher_login)

        emailEditText.setText("")
        passwordEditText.setText("")
        teacherCodeEditText.setText("")
    }
}

