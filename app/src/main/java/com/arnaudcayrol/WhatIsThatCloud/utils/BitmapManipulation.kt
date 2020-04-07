package com.arnaudcayrol.WhatIsThatCloud.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileOutputStream

object BitmapManipulation {


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



    fun saveBitmapToJPG(context: Context, bmp: Bitmap): File {
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val f =  File.createTempFile("tempfile", ".jpg", storageDirectory)
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()
        return f
    }


    fun resize(bitmap: Bitmap): Bitmap {
        val maxHeight = 1024
        val maxWidth = 1024
        val scale = Math.min(
            maxHeight.toFloat() / bitmap.width,
            maxWidth.toFloat() / bitmap.height
        )

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        val resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return resizedBitmap
    }


    fun resizeTo224(context: Context, selectedPhotoUri: Uri): File {
        val bitmap =
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, selectedPhotoUri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        val bitmap224 = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        return FileManipluation.saveBitmapToJPG(context, bitmap224)
    }


}

