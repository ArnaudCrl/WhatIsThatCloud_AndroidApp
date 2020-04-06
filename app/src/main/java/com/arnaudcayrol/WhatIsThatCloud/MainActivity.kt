package com.arnaudcayrol.WhatIsThatCloud

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.arnaudcayrol.WhatIsThatCloud.network.API_obj
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


private const val REQUEST_CODE_TAKE_PICTURE = 1
private const val REQUEST_CODE_SELECT_PICTURE = 2
private lateinit var photoFile: File
private lateinit var photoFile224: File


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
                btnDisplayResult?.setBackgroundColor(Color.parseColor("#a1a1a1"))
            } else {
                Toast.makeText(this, "No file to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        btnDisplayResult?.setBackgroundColor(Color.WHITE)
        btnDisplayResult?.isEnabled = true
        txtUserNotification.text = ""
    }


    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile("tempPhoto.jpg")

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

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var uri: Uri? = null

        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            uri = Uri.fromFile(photoFile)
        }

        if (requestCode == REQUEST_CODE_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            uri = data?.data!!
            photoFile = saveImageToTempFile(uri, "tempPhoto.jpg")
        }

        else super.onActivityResult(requestCode, resultCode, data)

        imageView.setImageURI(uri)
        if (uri != null) photoFile224 = resizeTo224(uri)
    }


    private fun saveImageToTempFile(uri: Uri, filename: String) : File{
        val file = getPhotoFile(filename)
        contentResolver.openInputStream(uri)?.copyTo(file.outputStream())
        return file
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
                btnDisplayResult?.setBackgroundColor(Color.WHITE)
                btnDisplayResult?.isEnabled = true
                txtUserNotification.text = ""
            }
        }
    }

    private fun saveBitmapToJPG(bmp: Bitmap): File {
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val f =  File.createTempFile("tempfile224", ".jpg", storageDirectory)
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()
        return f
    }

    private fun resizeTo224(selectedPhotoUri: Uri): File {
        val bitmap =
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri)
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        val bitmap224 = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        return saveBitmapToJPG(bitmap224)
    }

}




