package com.example.mad_cv_sp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main2)

        val teacherLogin = findViewById<TextView>(R.id.teacher_login)
        val studentLogin = findViewById<TextView>(R.id.student_login)

        teacherLogin.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        studentLogin.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }
    }
}
