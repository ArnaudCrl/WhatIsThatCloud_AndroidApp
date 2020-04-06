package com.arnaudcayrol.WhatIsThatCloud

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.arnaudcayrol.WhatIsThatCloud.network.API_obj
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.ColorUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*
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


class ResultActivity : AppCompatActivity() {

    lateinit var imageBitmap: Bitmap
    val recycler_view_clouds: ArrayList<Bitmap> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val cloudList = intent.getSerializableExtra("CloudList") as? CloudList
        val photoPath = intent.getSerializableExtra("UserPicture") as? String

        imageBitmap = BitmapFactory.decodeFile(photoPath)

        picUserPicture.setImageBitmap(imageBitmap)

        if (cloudList != null) {
            writeColoredText(result1, cloudList.resultList[0])
            setRecyclerList(cloudList.resultList[0].first)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uploadImage(saveBitmapToJPG(createScaledBitmap(imageBitmap, 500, 500, true)))
    }

    private fun uploadImage(photoFile224: File) {
        CoroutineScope(Job() + Dispatchers.Main ).launch {

            // Creating the request to the web server, sending a 224x224 px image
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile224)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", photoFile224?.name, fileReqBody)
            val getResultDeffered = API_obj.retrofitService.uploadFeedbackAsync(part)
            try {
                println("Success: ${getResultDeffered.await().Result}")
            } catch (e: Exception) {
//                Toast.makeText(applicationContext, "Failure: ${e.message}", Toast.LENGTH_LONG).show()
                println("Failure: ${e.message}")
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


    // Writes the result in the form :
    // CLOUD TYPE
    // XX% confidence -> colored text based on percentage
    private fun writeColoredText(textView: TextView, pair: Pair<String, Double>){
        textView.setText("${pair.first}\n" + "${(pair.second * 100).toInt()}% confidence", BufferType.SPANNABLE)
        val span = textView.text as Spannable
        span.setSpan(ForegroundColorSpan(ColorUtils.getColor(pair.second.toFloat())), pair.first.length, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(AbsoluteSizeSpan(20, true), pair.first.length, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }


    private fun setRecyclerList(best : String){

        if (best == "Altocumulus") addAltocumulus()
        if (best == "Altostratus") addAltostratus()
        if (best == "Cirrocumulus") addCirrocumulus()
        if (best == "Cirrostratus") addCirrostratus()
        if (best == "Cirrus") addCirrus()
        if (best == "Cumulonimbus") addCumulonimbus()
        if (best == "Cumulus") addCumulus()
        if (best == "Nimbostratus") addNimbostratus()
        if (best == "Stratocumulus") addStratocumulus()
        if (best == "Stratus") addStratus()

        // Creates a horizontal Layout Manager
        recyclerViewClouds.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

//        // Add a snap effect -> DOES NOT LOOK VERY GOOD
//        val snap : SnapHelper = PagerSnapHelper()
//        snap.attachToRecyclerView(recyclerViewClouds)

        // Access the RecyclerView Adapter and load the data into it
        recyclerViewClouds.adapter = RecyclerViewAdapter(recycler_view_clouds, this)
    }






    private fun addAltocumulus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altocumulus_9))
    }
    private fun addAltostratus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.altostratus_9))
    }
    private fun addCirrocumulus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrocumulus_9))
    }
    private fun addCirrostratus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrostratus_9))
    }
    private fun addCirrus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cirrus_9))
    }
    private fun addCumulonimbus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulonimbus_9))
    }
    private fun addCumulus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.cumulus_9))
    }
    private fun addNimbostratus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.nimbostratus_9))
    }
    private fun addStratocumulus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratocumulus_9))
    }
    private fun addStratus(){
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(resources, R.drawable.stratus_9))
    }

}





