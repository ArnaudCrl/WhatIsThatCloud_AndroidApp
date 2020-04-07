package com.arnaudcayrol.WhatIsThatCloud.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object FileManipluation {

    fun saveImageToTempFile(context: Context, uri: Uri, filename: String) : File {
        val file = getPhotoFile(context, filename)
        context.contentResolver.openInputStream(uri)?.copyTo(file.outputStream())
        return file
    }

    fun getPhotoFile(context: Context, fileName: String): File {
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    fun saveBitmapToJPG(context: Context, bitmap: Bitmap): File {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val f =  File.createTempFile("tempfile224", ".jpg", storageDirectory)
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()
        return f
    }

}