package com.arnaudcayrol.WhatIsThatCloud.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

import java.io.File

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

fun fileToBitmap(file : File) : Bitmap {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    return bitmap
}

fun scaleDownBitmap(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap? {
    val ratio = Math.min(
        maxImageSize / realImage.width,
        maxImageSize / realImage.height
    )
    val width = Math.round(ratio * realImage.width)
    val height = Math.round(ratio * realImage.height)
    return Bitmap.createScaledBitmap(realImage, width, height, filter)
}

fun setBitmapSize(realImage: Bitmap, imageSize: Int, filter: Boolean): Bitmap? {
    return Bitmap.createScaledBitmap(realImage, imageSize, imageSize, true)
}
