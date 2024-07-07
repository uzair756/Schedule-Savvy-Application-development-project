package com.example.mad_cv_sp

import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainActivity8 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main8)
        val galleryIconTextView: LinearLayout = findViewById(R.id.gallery_section)
        val chatIconImageView: LinearLayout = findViewById(R.id.messages_section)
        val bellIconImageView: LinearLayout = findViewById(R.id.alarm_section)


        galleryIconTextView.setOnClickListener {
            val intent = Intent(this, MainActivity13::class.java)
            startActivity(intent)
        }

        chatIconImageView.setOnClickListener {
            val intent = Intent(this, MainActivity9::class.java)
            startActivity(intent)
        }
        bellIconImageView.setOnClickListener {
            val intent = Intent(this, MainActivity10::class.java)
            startActivity(intent)
        }
    }


}