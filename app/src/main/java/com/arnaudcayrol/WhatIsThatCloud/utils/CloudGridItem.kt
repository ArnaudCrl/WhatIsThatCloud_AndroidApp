package com.arnaudcayrol.WhatIsThatCloud.utils

import android.content.Context
import android.util.Log
import com.arnaudcayrol.WhatIsThatCloud.R
import com.google.firebase.database.*
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.galery_cloud_grid_item.view.*

class CloudGridItem(val image_ref : String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref)
        ref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user_picture = p0.getValue(UserPicture::class.java) ?: return
                Picasso.get().load(user_picture.url).fit().into(viewHolder.itemView.cloud_image)
                viewHolder.itemView.txt_like_counter.text = user_picture.fav_count.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.galery_cloud_grid_item
    }
}