package com.arnaudcayrol.WhatIsThatCloud.utils

import android.content.res.Resources
import android.graphics.Bitmap
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import kotlinx.android.synthetic.main.activity_result.*

fun bitmapSquareCrop(bitmap : Bitmap): Bitmap? {
    if (bitmap.width >= bitmap.height){

        var dstBmp = Bitmap.createBitmap(
            bitmap,
            bitmap.width /2 - bitmap.height /2,
            0,
            bitmap.height,
            bitmap.height
        )
        return dstBmp

    }else{

        var dstBmp = Bitmap.createBitmap(
            bitmap,
            0,
            bitmap.height /2 - bitmap.width /2,
            bitmap.width,
            bitmap.width
        )
        return dstBmp
    }
}

fun roundBitmapEdge(bitmap : Bitmap): Bitmap? {
    val roundedImage = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
    roundedImage.cornerRadius = 100F
    return roundedImage.bitmap
}

fun circular_crop(bitmap : Bitmap): Bitmap? {
    val drawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap)
    drawable.isCircular = true
    return  drawable.bitmap
}
