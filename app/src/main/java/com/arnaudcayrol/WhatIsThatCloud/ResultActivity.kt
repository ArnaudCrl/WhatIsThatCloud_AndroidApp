package com.arnaudcayrol.WhatIsThatCloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
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
