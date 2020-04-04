package com.arnaudcayrol.WhatIsThatCloud

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.ColorUtils
import kotlinx.android.synthetic.main.activity_result.*


class ResultActivity : AppCompatActivity() {

    val animals: ArrayList<String> = ArrayList()
    val recycler_view_clouds: ArrayList<Bitmap> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val cloudList = intent.getSerializableExtra("CloudList") as? CloudList
        val photoPath = intent.getSerializableExtra("UserPicture") as? String

        var imageBitmap = BitmapFactory.decodeFile(photoPath)

//        imageBitmap = bitmapSquareCrop(imageBitmap)
//        imageBitmap = roundBitmapEdge(imageBitmap)

        picUserPicture.setImageBitmap(imageBitmap)

        if (cloudList != null) {
            writeColoredText(result1, cloudList.resultList[0])
        }

        setRecyclerList()
    }




    private fun setRecyclerList(){
        // Loads animals into the ArrayList
        addCumulus()



        // Creates a horizontal Layout Manager
        recyclerViewClouds.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        // Add a snap effect
        val snap : SnapHelper = PagerSnapHelper()
        snap.attachToRecyclerView(recyclerViewClouds)

        // Access the RecyclerView Adapter and load the data into it
        recyclerViewClouds.adapter = RecyclerViewAdapter(recycler_view_clouds, this)
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

    private fun addAltocumulus(){
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
    private fun addAltostratus(){
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
    private fun addCirrocumuls(){
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
    private fun addCirrostratus(){
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
    private fun addCirrus(){
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
    private fun addCumulonimbus(){
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
    private fun addStratocumulus(){
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
    private fun addStratus(){
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

}





