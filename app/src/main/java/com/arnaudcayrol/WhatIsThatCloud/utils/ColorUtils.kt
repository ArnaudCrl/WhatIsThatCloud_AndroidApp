package com.arnaudcayrol.WhatIsThatCloud.utils

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.TextView
import com.arnaudcayrol.WhatIsThatCloud.R
import kotlin.math.roundToInt

object ColorUtils {
    private const val FIRST_COLOR: Int = 0xff8B0000.toInt() // DARK RED
    private const val SECOND_COLOR: Int = 0xffDAA520.toInt() // ORANGE
    private const val THIRD_COLOR: Int = 0xff32CD32.toInt() // LIME GREEN

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
        return src + (p * (dst - src)).roundToInt()
    }

    // Writes the result in the form :
    // CLOUD TYPE
    // XX% confidence -> colored text based on percentage
    fun writeColoredResultText(context : Context, textView: TextView, pair: Pair<String, Double>){
        var confidence = context.getString(R.string.confidence)
        textView.setText("${pair.first}\n" + "${(pair.second * 100).toInt()}% " + confidence, TextView.BufferType.SPANNABLE)
        val span = textView.text as Spannable
        span.setSpan(ForegroundColorSpan(getColor(pair.second.toFloat())), pair.first.length, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(RelativeSizeSpan(0.7f), pair.first.length, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    }
}