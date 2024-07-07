package com.example.mad_cv_sp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Delay for 5 seconds before starting MainActivity2
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 5000 milliseconds = 5 seconds
    }
}