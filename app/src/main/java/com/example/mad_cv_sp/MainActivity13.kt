package com.example.mad_cv_sp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity13 : AppCompatActivity() {

    private val PICK_IMAGE = 1
    private val REQUEST_CODE_PERMISSION = 2
    private lateinit var imageView: ImageView
    private lateinit var noImageTextView: TextView
    private lateinit var selectImageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main13)

        imageView = findViewById(R.id.imageView)
        noImageTextView = findViewById(R.id.noImageTextView)
        selectImageButton = findViewById(R.id.selectImageButton)

        // Request necessary permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION)
        }

        selectImageButton.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let {
                try {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    imageView.setImageBitmap(bitmap)
                    imageView.visibility = View.VISIBLE
                    noImageTextView.visibility = View.GONE
                    UploadImageTask().execute(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inner class UploadImageTask : AsyncTask<Bitmap, Void, String>() {

        override fun doInBackground(vararg bitmaps: Bitmap): String? {
            val bitmap = bitmaps[0]
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes: ByteArray = baos.toByteArray()
            val encodedImage: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            val urlString = "http://192.168.245.154:5000/extract_timetable"

            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.doInput = true

                val jsonParam = JSONObject()
                jsonParam.put("image", encodedImage)

                Log.d("UploadImageTask", "Sending request to $urlString")

                conn.outputStream.write(jsonParam.toString().toByteArray(charset("UTF-8")))

                Log.d("UploadImageTask", "Request sent")

                val inputStream: InputStream = conn.inputStream
                val response = StringBuilder()
                var data: Int
                while (inputStream.read().also { data = it } != -1) {
                    response.append(data.toChar())
                }
                Log.d("UploadImageTask", "Response received")
                return response.toString()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("UploadImageTask", "Error: ${e.message}")
                return null
            } catch (e: JSONException) {
                e.printStackTrace()
                Log.e("UploadImageTask", "Error: ${e.message}")
                return null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            result?.let {
                Log.d("Response", it)
                try {
                    val jsonObject = JSONObject(it)
                    val timetable = jsonObject.getString("timetable")
                    Toast.makeText(this@MainActivity13, timetable, Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } ?: run {
                Toast.makeText(this@MainActivity13, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
