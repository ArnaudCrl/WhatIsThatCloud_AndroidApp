package com.arnaudcayrol.WhatIsThatCloud.fragments

import android.R.attr.name
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.arnaudcayrol.WhatIsThatCloud.GalleryFocus
import com.arnaudcayrol.WhatIsThatCloud.R
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_global_gallery.*
import kotlinx.android.synthetic.main.fragment_my_gallery.*


class MyGalleryFragment : Fragment(R.layout.fragment_my_gallery) {

    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview
    private val uid = FirebaseAuth.getInstance().uid
    val imagelist  = sortedMapOf<Long, CloudGridItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txt_no_observation_in_gallery.isVisible = false
        updateGallery()

        adapter.setOnItemClickListener { item, view ->

            val extra = item as CloudGridItem
            val item_ref = extra.ref

            item_ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val intent = Intent(activity, GalleryFocus::class.java)
                        intent.putExtra("picture", extra.image_ref)
                        startActivity(intent)
                    } else {
                        updateGallery()
                        Toast.makeText(context, "Cette photo n'existe plus :(", Toast.LENGTH_SHORT).show() }
                    }

                override fun onCancelled(error: DatabaseError) {
                    // Failed, how to handle?
                }
            })
        }



        my_gallery_swipe_refresh.setOnRefreshListener {
            updateGallery()
            my_gallery_swipe_refresh.isRefreshing = false
        }

    }

    private fun updateGallery(){
        adapter.clear()
        imagelist.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.child("pictures").children.forEach{pictures -> // Iterate over pictures
                    val database_ref = pictures.ref.toString()
                    val date_uploaded = pictures.child("date_uploaded").value
                    imagelist[(date_uploaded as Long) * -1] = CloudGridItem(database_ref)
                }

                adapter.update(imagelist.values)
                personnal_grid_layout.adapter = adapter
                txt_no_observation_in_gallery.isVisible = imagelist.size == 0
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })
    }

//    override fun onResume() {
//        updateGalery()
//        super.onResume()
//    }

}