package com.arnaudcayrol.WhatIsThatCloud

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.arnaudcayrol.WhatIsThatCloud.network.API_obj
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.BitmapManipulation
import com.arnaudcayrol.WhatIsThatCloud.utils.FileManipluation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.*
import retrofit2.Call
import java.io.File


private const val REQUEST_CODE_TAKE_PICTURE = 1
private const val REQUEST_CODE_SELECT_PICTURE = 2
private lateinit var photoFile: File
private lateinit var photoFile224: File


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        CoroutineScope(Job() + Dispatchers.Main ).launch {
            try {
                API_obj.retrofitService.wakeupServer().await()
            }
            catch (e: Exception) {
                // This wakes up the server to gain time, so it is supposed to throw an exception
            }
        }

        btnTakePic.setOnClickListener{
            takePicture()
        }

        btnOpenGalery.setOnClickListener{
            openGalery()
        }

        btnDisplayResult.setOnClickListener{
            if (::photoFile.isInitialized) {
                uploadImage()
                btnDisplayResult?.isEnabled = false
                btnDisplayResult.setBackgroundResource(R.drawable.oval_grey_button)
                btnDisplayResult.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorTextGrey))


            } else {
                Toast.makeText(this, "No file to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::photoFile.isInitialized) {
            btnDisplayResult.setBackgroundResource(R.drawable.oval_orange_button)
            btnDisplayResult.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorTextDark))
            btnDisplayResult?.isEnabled = true
            txtUserNotification.text = ""
        }
    }


    private fun uploadImage() {
        txtUserNotification.text = "Awaiting server Response ..."
        CoroutineScope(Job() + Dispatchers.Main ).launch {

            // Creating the request to the web server, sending a 224x224 px image
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile224)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", photoFile224?.name, fileReqBody)
            val getPropertiesDeferred = API_obj.retrofitService.uploadFileAsync(part)

            try {
                // Await the completion of our Retrofit request
                val cloudList : CloudList = getPropertiesDeferred.await()

                // Passing the result to ResultActivity
                val intent = Intent(applicationContext, ResultActivity::class.java)
                intent.putExtra("CloudList", cloudList)
                intent.putExtra("UserPicture", photoFile.absolutePath)
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Failure: ${e.message}", Toast.LENGTH_LONG).show()
                println("Failure: ${e.message}")
                btnDisplayResult.setBackgroundResource(R.drawable.oval_orange_button)
                btnDisplayResult.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorTextDark))

                btnDisplayResult?.isEnabled = true
                txtUserNotification.text = ""
            }
        }
    }


    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = FileManipluation.getPhotoFile(this, "tempPhoto.jpg")

        val fileProvider = FileProvider.getUriForFile(this, "com.arnaudcayrol.WhatIsThatCloud.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE)
        } else {
            Toast.makeText(this, "unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGalery() {
        val openGaleryIntent = Intent()
        openGaleryIntent.type = "image/*"
        openGaleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(openGaleryIntent, "Select image"), REQUEST_CODE_SELECT_PICTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var uri: Uri? = null

        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            uri = Uri.fromFile(photoFile)
        }

        if (requestCode == REQUEST_CODE_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            uri = data?.data!!
            photoFile = FileManipluation.saveImageToTempFile(this, uri, "tempPhoto.jpg")
        }

        else super.onActivityResult(requestCode, resultCode, data)

        btnDisplayResult.setBackgroundResource(R.drawable.oval_orange_button)
        btnDisplayResult.setTextColor(ContextCompat.getColor(applicationContext, R.color.colorTextDark))

        imageView.setImageURI(uri)
        img_BakgroundSketch.visibility = View.GONE
        if (uri != null) photoFile224 = BitmapManipulation.resizeTo224(this, uri)
    }
}






