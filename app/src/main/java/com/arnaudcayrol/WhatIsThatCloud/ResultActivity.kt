package com.arnaudcayrol.WhatIsThatCloud

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RadioButton
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
    private var feedbackChoice: String = "NOFEEDBACK"
    private var userHasClicked : Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val cloudList = intent.getSerializableExtra("CloudList") as? CloudList
        val photoPath = intent.getSerializableExtra("UserPicture") as? String

        imageBitmap = BitmapFactory.decodeFile(photoPath)

        picUserPicture.setImageBitmap(imageBitmap)

        if (cloudList != null) {
            ColorUtils.writeColoredResultText(result1, cloudList.resultList[0])
            SetupRecyclerList.setRecyclerList(this, recyclerViewClouds, cloudList.resultList[0].first)
        }

        feedback_choice.visibility = View.GONE

        btn_giveFeedback.setOnClickListener{
            if (!userHasClicked){
                userHasClicked = true
                feedback_choice.visibility = View.VISIBLE
            } else if (userHasClicked){
                userHasClicked = false
                feedback_choice.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (feedbackChoice == "NOFEEDBACK") uploadImage(BitmapManipulation.saveBitmapToJPG(this, BitmapManipulation.resize(imageBitmap)))
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.choice_correct ->
                    if (checked) {
                        feedbackChoice = "CORRECT"
                    }
                R.id.choice_dont_know ->
                    if (checked) {
                        feedbackChoice = "UNKNOWN"
                    }
                R.id.choice_incorrect ->
                    if (checked) {
                        feedbackChoice = "WRONG"
                    }
            }
        }
        Handler().postDelayed({
            feedback_choice.visibility = View.GONE
            btn_giveFeedback.visibility = View.GONE
            uploadImage(BitmapManipulation.saveBitmapToJPG(this, BitmapManipulation.resize(imageBitmap)))
            Toast.makeText(this, "Thank you for your feedback", Toast.LENGTH_LONG).show()
        }, 1000)
    }



    private fun uploadImage(photoFile224: File) {
        CoroutineScope(Job() + Dispatchers.Main ).launch {

            // Creating the request to the web server, sending a 224x224 px image
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile224)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", feedbackChoice, fileReqBody)
            val getResultDeffered = API_obj.retrofitService.uploadFeedbackAsync(part)
            try {
                println("Success: ${getResultDeffered.await().Result}")
            } catch (e: Exception) {
//                Toast.makeText(applicationContext, "Failure: ${e.message}", Toast.LENGTH_LONG).show()
                println("Failure: ${e.message}")
            }
        }
    }
}





