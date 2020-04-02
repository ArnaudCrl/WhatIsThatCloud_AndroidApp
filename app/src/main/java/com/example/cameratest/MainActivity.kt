package com.example.cameratest

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cameratest.network.API_obj
import com.example.cameratest.network.CloudList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


private const val REQUEST_CODE_TAKE_PICTURE = 1
private const val REQUEST_CODE_SELECT_PICTURE = 2
private lateinit var photoFile: File
private const val FILE_NAME = "tempPhoto.jpg"



class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                btnDisplayResult?.setBackgroundColor(Color.parseColor("#a1a1a1"));
            } else {
                Toast.makeText(this, "No file to upload", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        btnDisplayResult?.setBackgroundColor(Color.WHITE);
        btnDisplayResult?.isEnabled = true
        txtUserNotification.text = ""
    }


    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME)

        val fileProvider = FileProvider.getUriForFile(this, "com.example.cameratest.fileprovider", photoFile)
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

    private fun getPhotoFile(fileName: String): File {
        val StorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", StorageDirectory)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        }

        if (requestCode == REQUEST_CODE_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                saveImageToTempFile(uri)
            }
            imageView.setImageURI(uri)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun saveImageToTempFile(uri: Uri){
        photoFile = getPhotoFile(FILE_NAME)
        contentResolver.openInputStream(uri)?.copyTo(photoFile.outputStream())
    }


//    private var viewModelJob = Job()
//    private val coroutineScope = 

    private fun uploadImage() {
        txtUserNotification.text = "Awaiting server Response ..."
        CoroutineScope(Job() + Dispatchers.Main ).launch {

            // Creating the request to the web server
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile);
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", photoFile.getName(), fileReqBody)
            val getPropertiesDeferred = API_obj.retrofitService.uploadFile(part)

            try {
                // Await the completion of our Retrofit request
                val cloudList : CloudList = getPropertiesDeferred.await()

                // Passing the result to ResultActivity
                val intent = Intent(getApplicationContext(), ResultActivity::class.java)
                intent.putExtra("CloudList", cloudList)
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(getApplicationContext(), "Failure: ${e.message}", Toast.LENGTH_LONG).show()
                println("Failure: ${e.message}")
                btnDisplayResult?.setBackgroundColor(Color.WHITE);
                btnDisplayResult?.isEnabled = true
                txtUserNotification.text = ""
            }
        }

    }

}




