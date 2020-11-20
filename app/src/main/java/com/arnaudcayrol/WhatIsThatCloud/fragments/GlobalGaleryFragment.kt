package com.arnaudcayrol.WhatIsThatCloud.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.arnaudcayrol.WhatIsThatCloud.GalleryFocus
import com.arnaudcayrol.WhatIsThatCloud.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_global_galery.*


class GlobalGaleryFragment : Fragment(R.layout.fragment_global_galery) {

    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateGalery()
        super.onViewCreated(view, savedInstanceState)

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(activity, GalleryFocus::class.java)
            val extra = item as CloudGridItem
            intent.putExtra("picture", extra.image_ref)
            startActivity(intent)
        }
    }

    private fun updateGalery(){
        adapter.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {user -> // Iterate over users
                    user.child("pictures").children.forEach{pictures -> // Iterate over pictures
                        adapter.add(CloudGridItem(pictures.ref.toString()))
                    }
                }
                grid_layout.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })
    }

}