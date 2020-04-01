package com.example.cameratest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


private const val REQUEST_CODE_TAKE_PICTURE = 1
private const val REQUEST_CODE_SELECT_PICTURE = 2
private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"



class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TAKE PICTURE
        btnTakePic.setOnClickListener{
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

        // OPEN GALERY
        btnOpenGalery.setOnClickListener{
            val openGaleryIntent = Intent()
            openGaleryIntent.type = "image/*"
            openGaleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(openGaleryIntent, "Select image"), REQUEST_CODE_SELECT_PICTURE)
        }

        getMarsRealEstateProperties()
        // OPEN GALERY
        btnDisplayResult.setOnClickListener{
            println("LA REPONSE EST :")
            println(result)
            txt_testResult.text = result
        }

    }

    private fun getPhotoFile(fileName: String): File {
        val StorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", StorageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            // val imageBitmap = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == REQUEST_CODE_SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data
            imageView.setImageURI(uri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    // The internal MutableLiveData String that stores the most recent response
    private var _response = MutableLiveData<String>()
    private var result : String? = ""
    // The external immutable LiveData for the response String
//    val response: LiveData<String>
////        get() = _response

    private fun getMarsRealEstateProperties() {
        MarsApi.retrofitService.getProperties().enqueue(
            object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    result = "Failure: " + t.message
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    println("success")
                    result = response.body()
                    println(result)
                }
            })
    }



}




