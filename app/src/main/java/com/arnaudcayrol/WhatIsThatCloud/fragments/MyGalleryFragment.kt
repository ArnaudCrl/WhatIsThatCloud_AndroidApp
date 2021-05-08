package com.arnaudcayrol.WhatIsThatCloud.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.arnaudcayrol.WhatIsThatCloud.GallerySwipeActivity
import com.arnaudcayrol.WhatIsThatCloud.R
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_my_gallery.*


class MyGalleryFragment : Fragment(R.layout.fragment_my_gallery) {

    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview
    private val uid = FirebaseAuth.getInstance().uid
    val imagelist  = sortedMapOf<Long, CloudGridItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txt_no_observation_in_gallery.isVisible = false
        updateGallery()

        adapter.setOnItemClickListener { item, _ ->

            val images_ref_list : ArrayList<String> = ArrayList()
            imagelist.forEach() {
                images_ref_list.add(it.value.image_ref)
            }

            val cloud_grid_item = item as CloudGridItem
            val item_ref = cloud_grid_item.ref

            item_ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val intent = Intent(activity, GallerySwipeActivity::class.java)
//                        intent.putExtra("picture", extra.image_ref)
                        intent.putExtra("pictures_ref", images_ref_list)
                        intent.putExtra("current_ref", cloud_grid_item.image_ref)
//                        Log.d("swipe_test", images_ref_list.size.toString())

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
                if(!p0.exists()) { return }
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