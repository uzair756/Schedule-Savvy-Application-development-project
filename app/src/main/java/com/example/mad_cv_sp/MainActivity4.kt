package com.example.mad_cv_sp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity4 : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        database = FirebaseDatabase.getInstance().getReference("students")

        val accountExistTextView = findViewById<TextView>(R.id.account_student_login_exist)
        val loginTextView = findViewById<TextView>(R.id.student_login)

        accountExistTextView.setOnClickListener {
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
        }

        loginTextView.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val regNo = findViewById<EditText>(R.id.registration_student_login).text.toString().trim()
        val password = findViewById<EditText>(R.id.password_student_login).text.toString().trim()

        if (regNo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        checkCredentials(regNo, password)
    }

    private fun checkCredentials(regNo: String, password: String) {
        database.orderByChild("regNo").equalTo(regNo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (studentSnapshot in dataSnapshot.children) {
                        val storedPassword = studentSnapshot.child("password").getValue(String::class.java)

                        if (storedPassword == password) {
                            Toast.makeText(this@MainActivity4, "User logged in successfully", Toast.LENGTH_SHORT).show()
                            clearInputFields()
                            val intent = Intent(this@MainActivity4, MainActivity8::class.java)
                            startActivity(intent)
                            return
                        } else {
                            Toast.makeText(this@MainActivity4, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity4, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity4, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearInputFields() {
        val regNoEditText = findViewById<EditText>(R.id.registration_student_login)
        val passwordEditText = findViewById<EditText>(R.id.password_student_login)

        regNoEditText.setText("")
        passwordEditText.setText("")
    }
}
