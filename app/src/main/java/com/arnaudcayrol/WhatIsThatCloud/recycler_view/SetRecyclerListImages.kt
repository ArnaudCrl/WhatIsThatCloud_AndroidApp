package com.arnaudcayrol.WhatIsThatCloud.recycler_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arnaudcayrol.WhatIsThatCloud.R

object SetupRecyclerList {
    val recycler_view_clouds: ArrayList<Bitmap> = ArrayList()

    fun setRecyclerList(context: Context, recyclerview: RecyclerView, best: String){

        if (best == "Altocumulus") addAltocumulus(context)
        if (best == "Altostratus") addAltostratus(context)
        if (best == "Cirrocumulus") addCirrocumulus(context)
        if (best == "Cirrostratus") addCirrostratus(context)
        if (best == "Cirrus") addCirrus(context)
        if (best == "Cumulonimbus") addCumulonimbus(context)
        if (best == "Cumulus") addCumulus(context)
        if (best == "Nimbostratus") addNimbostratus(context)
        if (best == "Stratocumulus") addStratocumulus(context)
        if (best == "Stratus") addStratus(context)

        
        // Creates a horizontal Layout Manager
        recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        // Access the RecyclerView Adapter and load the data into it
        recyclerview.adapter = RecyclerViewAdapter(recycler_view_clouds, context)
    }

    private fun addAltocumulus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altocumulus_9))
    }
    private fun addAltostratus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.altostratus_9))
    }
    private fun addCirrocumulus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrocumulus_9))
    }
    private fun addCirrostratus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrostratus_9))
    }
    private fun addCirrus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cirrus_9))
    }
    private fun addCumulonimbus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulonimbus_9))
    }
    private fun addCumulus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.cumulus_9))
    }
    private fun addNimbostratus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.nimbostratus_9))
    }
    private fun addStratocumulus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratocumulus_9))
    }
    private fun addStratus(context: Context){
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_1))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_2))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_3))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_4))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_5))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_6))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_7))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_8))
        recycler_view_clouds.add(BitmapFactory.decodeResource(context.resources, R.drawable.stratus_9))
    }
}