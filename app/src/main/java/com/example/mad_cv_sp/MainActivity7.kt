package com.example.mad_cv_sp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity7 : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var messagesContainer: LinearLayout
    private lateinit var messageInput: EditText
    private lateinit var sendButton: TextView
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main7)

        database = FirebaseDatabase.getInstance().getReference("messages")
        messagesContainer = findViewById(R.id.messages_container)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)

        userName = intent.getStringExtra("userName") ?: "Unknown"

        fetchMessages()

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun fetchMessages() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesContainer.removeAllViews()
                for (messageSnapshot in dataSnapshot.children) {
                    val name = messageSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val index = messageSnapshot.child("index").getValue(String::class.java) ?: "Unknown"
                    val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java) ?: 0

                    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val currentTime = sdf.format(Date(timestamp))

                    val messageLayout = LinearLayout(this@MainActivity7)
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, 0, 0, 16)
                    messageLayout.layoutParams = layoutParams
                    messageLayout.orientation = LinearLayout.VERTICAL
                    messageLayout.setBackgroundResource(R.drawable.rounded_bg_light_green)

                    val nameTextView = TextView(this@MainActivity7)
                    nameTextView.text = name
                    nameTextView.setTextColor(Color.BLACK)
                    nameTextView.setTypeface(null, Typeface.BOLD)
                    nameTextView.setPadding(16, 8, 16, 0)
                    val nameParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    nameParams.gravity = Gravity.START
                    nameTextView.layoutParams = nameParams

                    val timeTextView = TextView(this@MainActivity7)
                    timeTextView.text = currentTime
                    timeTextView.setTextColor(Color.BLACK)
                    timeTextView.setPadding(16, 0, 16, 8)
                    val timeParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    timeParams.gravity = Gravity.END
                    timeTextView.layoutParams = timeParams

                    val messageTextView = TextView(this@MainActivity7)
                    messageTextView.text = index
                    messageTextView.setTextColor(Color.BLUE)
                    messageTextView.setPadding(16, 0, 16, 8)
                    val messageParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    messageParams.gravity = Gravity.START
                    messageTextView.layoutParams = messageParams

                    messageLayout.addView(nameTextView)
                    messageLayout.addView(timeTextView)
                    messageLayout.addView(messageTextView)

                    messagesContainer.addView(messageLayout)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val messageId = database.push().key
            val message = mapOf(
                "name" to userName,
                "index" to messageText,
                "timestamp" to System.currentTimeMillis(),
                "day" to SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
            )

            if (messageId != null) {
                database.child(messageId).setValue(message)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            messageInput.text.clear()
                        } else {
                            // Handle failure
                        }
                    }
            }
        }
    }
}
