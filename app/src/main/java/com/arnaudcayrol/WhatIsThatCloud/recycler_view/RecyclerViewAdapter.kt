package com.arnaudcayrol.WhatIsThatCloud.recycler_view

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arnaudcayrol.WhatIsThatCloud.R
import kotlinx.android.synthetic.main.cloud_list_item.view.*


class RecyclerViewAdapter (val imageBitmapList : ArrayList<Bitmap>, val context: Context) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.cloud_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.image?.setImageBitmap(imageBitmapList[position])
    }

    override fun getItemCount(): Int {
        return imageBitmapList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.cloud_image
    }
}
