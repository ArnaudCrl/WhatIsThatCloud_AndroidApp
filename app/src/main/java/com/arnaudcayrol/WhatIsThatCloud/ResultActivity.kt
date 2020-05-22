package com.arnaudcayrol.WhatIsThatCloud

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.network.API_obj
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.recycler_view.SetupRecyclerList
import com.arnaudcayrol.WhatIsThatCloud.utils.BitmapManipulation
import com.arnaudcayrol.WhatIsThatCloud.utils.ColorUtils
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ResultActivity : AppCompatActivity() {

    lateinit var imageBitmap: Bitmap
    lateinit var cloudList: CloudList
    var pageNumber : Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        cloudList = (intent.getSerializableExtra("CloudList") as? CloudList)!!
        val photoPath = intent.getSerializableExtra("UserPicture") as? String

        imageBitmap = BitmapFactory.decodeFile(photoPath)

        picUserPicture.setImageBitmap(imageBitmap)

        btn_left_arrow.isEnabled = false
        btn_left_arrow.setImageResource(R.drawable.left_arrow_off)
        ColorUtils.writeColoredResultText(txt_result, cloudList.resultList[0])
        SetupRecyclerList.setRecyclerList(this, recyclerViewClouds, cloudList.resultList[0].first)

        val alert = SetupConfirmationDialog()

        btn_left_arrow.setOnClickListener {
            GoToPreviousPrediction()
        }
        btn_right_arrow.setOnClickListener {
            GoToNextPrediction()
        }
        btn_validationfeedback.setOnClickListener {
            alert.show()
        }
        btn_wikiRedirect.setOnClickListener {
            GoToURL(cloudList.urlList[cloudList.resultList[pageNumber].first])
        }
    }

    private fun SetupConfirmationDialog(): AlertDialog{
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm")
        builder.setMessage("Is this result correct?")

        builder.setPositiveButton("YES"
        ) { dialog, _ ->
            btn_validationfeedback.isEnabled = false
            btn_validationfeedback.setImageResource(R.drawable.validation_icon_clicked)
            Handler().postDelayed({
                Toast.makeText(this, "Thank you for your feedback", Toast.LENGTH_LONG).show()
            }, 1000)
            uploadImage(BitmapManipulation.saveBitmapToJPG(this, BitmapManipulation.resizeTo1024px(imageBitmap)))
            dialog.dismiss()
        }

        builder.setNegativeButton("NO"
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        return builder.create()
    }


    private fun GoToNextPrediction() {
        if (pageNumber == 0){
            btn_left_arrow.isEnabled = true
            btn_left_arrow.setImageResource(R.drawable.left_arrow_on)
        }
        if (pageNumber == cloudList.resultList.size - 2) {
            btn_right_arrow.isEnabled = false
            btn_right_arrow.setImageResource(R.drawable.right_arrow_off)
        }
        pageNumber++
        SetupRecyclerList.resetRecyclerView()
        ColorUtils.writeColoredResultText(txt_result, cloudList.resultList[pageNumber])
        SetupRecyclerList.setRecyclerList(this, recyclerViewClouds, cloudList.resultList[pageNumber].first)
    }

    private fun GoToPreviousPrediction() {
        if (pageNumber == cloudList.resultList.size - 1){
            btn_right_arrow.isEnabled = true
            btn_right_arrow.setImageResource(R.drawable.right_arrow_on)
        }
        if (pageNumber == 1) {
            btn_left_arrow.isEnabled = false
            btn_left_arrow.setImageResource(R.drawable.left_arrow_off)
        }
        pageNumber--
        SetupRecyclerList.resetRecyclerView()
        ColorUtils.writeColoredResultText(txt_result, cloudList.resultList[pageNumber])
        SetupRecyclerList.setRecyclerList(this, recyclerViewClouds, cloudList.resultList[pageNumber].first)
    }

    override fun onDestroy() {
        super.onDestroy()
        SetupRecyclerList.resetRecyclerView()
    }


    private fun uploadImage(photoFile224: File) {
        CoroutineScope(Job() + Dispatchers.Main ).launch {

            // Creating the request to the web server, sending a 224x224 px image
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile224)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", cloudList.resultList[pageNumber].first, fileReqBody)
            val getResultDeffered = API_obj.retrofitService.uploadFeedbackAsync(part)
            try {
                println("Success: ${getResultDeffered.await().Result}")
            } catch (e: Exception) {
//                Toast.makeText(applicationContext, "Failure: ${e.message}", Toast.LENGTH_LONG).show()
                println("Failure: ${e.message}")
            }
        }
    }

    fun GoToURL(url: String?) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }



}





