package com.arnaudcayrol.WhatIsThatCloud.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.arnaudcayrol.WhatIsThatCloud.GalleryFocus
import com.arnaudcayrol.WhatIsThatCloud.R
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_global_gallery.*


class GlobalGalleryFragment : Fragment(R.layout.fragment_global_gallery) {

    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview
    val imagelist  = sortedMapOf<Long, CloudGridItem>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateGallery()
        super.onViewCreated(view, savedInstanceState)

        adapter.setOnItemClickListener { item, _ ->

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

        global_gallery_swipe_refresh.setOnRefreshListener {
            updateGallery()
            global_gallery_swipe_refresh.isRefreshing = false
        }

    }

    private fun updateGallery(){
        adapter.clear()
        imagelist.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {user -> // Iterate over users

                    user.child("pictures").children.forEach{pictures -> // Iterate over pictures
                        val database_ref = pictures.ref.toString()
                        val date_uploaded = pictures.child("date_uploaded").value
                        imagelist[(date_uploaded as Long) * -1] = CloudGridItem(database_ref) // reversing the order so that nex pics are shown first
                    }
                }
                adapter.update(imagelist.values)
                global_grid_layout.adapter = adapter
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