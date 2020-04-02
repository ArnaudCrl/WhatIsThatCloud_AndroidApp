package com.arnaudcayrol.WhatIsThatCloud

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import kotlinx.android.synthetic.main.activity_result.*


class ResultActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val cloudList = intent.getSerializableExtra("CloudList") as? CloudList

        if (cloudList != null) {

            writeColoredText(result1, cloudList.result_list[0])
            writeColoredText(result2, cloudList.result_list[1])
            writeColoredText(result3, cloudList.result_list[2])

//            result1.text =  "${cloudList.result_list.get(0).first} with ${(cloudList.result_list[0].second * 100).toInt()}% confidence\n"
//            result1.setTextColor(ColorUtils.getColor(cloudList.result_list[0].second.toFloat()));

//            result2.text =  "${cloudList.result_list.get(1).first} with ${(cloudList.result_list[1].second * 100).toInt()}% confidence\n"
//            result2.setTextColor(ColorUtils.getColor(cloudList.result_list[1].second.toFloat()))
//
//            result3.text =  "${cloudList.result_list.get(2).first} with ${(cloudList.result_list[2].second * 100).toInt()}% confidence\n"
//            result3.setTextColor(ColorUtils.getColor(cloudList.result_list[2].second.toFloat()))
        }
    }

    private fun writeColoredText(textView: TextView, pair: Pair<String, Double>){
        textView.setText("${pair.first} with ${(pair.second * 100).toInt()}% confidence", BufferType.SPANNABLE)
        val span = textView.text as Spannable
        span.setSpan(ForegroundColorSpan(ColorUtils.getColor(pair.second.toFloat())), 0, pair.first.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

}


object ColorUtils {
    private val FIRST_COLOR: Int = Color.RED
    private val SECOND_COLOR: Int = Color.YELLOW
    private val THIRD_COLOR: Int = Color.GREEN
    fun getColor(p: Float): Int {
        var p = p
        val c0: Int
        val c1: Int
        if (p <= 0.5f) {
            p *= 2f
            c0 = FIRST_COLOR
            c1 = SECOND_COLOR
        } else {
            p = (p - 0.5f) * 2
            c0 = SECOND_COLOR
            c1 = THIRD_COLOR
        }
        val a = ave(Color.alpha(c0), Color.alpha(c1), p)
        val r = ave(Color.red(c0), Color.red(c1), p)
        val g = ave(Color.green(c0), Color.green(c1), p)
        val b = ave(Color.blue(c0), Color.blue(c1), p)
        return Color.argb(a, r, g, b)
    }

    private fun ave(src: Int, dst: Int, p: Float): Int {
        return src + Math.round(p * (dst - src))
    }
}