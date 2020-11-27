package com.arnaudcayrol.WhatIsThatCloud.utils

import android.util.Log
import com.arnaudcayrol.WhatIsThatCloud.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.gallery_cloud_grid_item.view.*
import kotlinx.android.synthetic.main.example_cloud_grid_item.view.*
import kotlinx.android.synthetic.main.ranking_item.view.*
import kotlinx.android.synthetic.main.result_grid_item.view.*
import kotlin.math.ln

class ExampleGridItem(val url : String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(url).resize(256,256).centerCrop().into(viewHolder.itemView.example_cloud_image)

    }
    override fun getLayout(): Int {
        return R.layout.example_cloud_grid_item
    }
}

class CloudGridItem(val image_ref : String): Item<ViewHolder>() {

    public val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref)

    override fun bind(viewHolder: ViewHolder, position: Int) {


        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user_picture = p0.getValue(UserPicture::class.java) ?: return
                Picasso.get().load(user_picture.url).resize(256,256).centerCrop().into(viewHolder.itemView.cloud_image)
                viewHolder.itemView.txt_like_counter.text = user_picture.fav_count.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.gallery_cloud_grid_item
    }
}

class ExampleResultItem(private val url : String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Picasso.get().load(url).resize(256,256).centerCrop().into(viewHolder.itemView.result_cloud_image)

    }
    override fun getLayout(): Int {
        return R.layout.result_grid_item
    }
}

class RankingItem(private val username : String, private val xp : String): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val level = (ln((xp.toInt() / 100).toDouble()) / ln(2.1) + 2).coerceAtLeast(1.0)

        viewHolder.itemView.txt_rank_level.text = level.toInt().toString()
        viewHolder.itemView.txt_rank_username.text = username

//        Log.d("level", "$username is level ${level}")

        when {
            level.toInt() == 1 -> {
                viewHolder.itemView.rank_progress_bar.progress = xp.toInt()
            }
            level.toInt() == 2 -> {
                viewHolder.itemView.rank_progress_bar.progress = (((xp.toDouble() - 100) / 120) * 100).toInt()
            }
            else -> {
                viewHolder.itemView.rank_progress_bar.progress = ((level - level.toInt()) * 100).toInt()
            }
        }

    }
    override fun getLayout(): Int {
        return R.layout.ranking_item
    }
}