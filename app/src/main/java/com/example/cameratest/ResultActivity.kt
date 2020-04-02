package com.example.cameratest

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cameratest.network.CloudList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val cloudList = intent.getSerializableExtra("CloudList") as? CloudList
//        println(cloudList.getBest())
        txtResult.text = cloudList?.getBest()

    }
}
