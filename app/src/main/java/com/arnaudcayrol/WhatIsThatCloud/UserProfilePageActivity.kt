package com.arnaudcayrol.WhatIsThatCloud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.arnaudcayrol.WhatIsThatCloud.utils.ExampleGridItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_user_profile_page.*

class UserProfilePageActivity : AppCompatActivity() {
    private lateinit var uid: String
    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview
    val imagelist  = sortedMapOf<Long, CloudGridItem>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_page)

        uid = (intent.getSerializableExtra("uid") as String)

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
                        val intent = Intent(this@UserProfilePageActivity, GallerySwipe::class.java)
//                        intent.putExtra("picture", extra.image_ref)
                        intent.putExtra("pictures_ref", images_ref_list)
                        intent.putExtra("current_ref", cloud_grid_item.image_ref)
//                        Log.d("swipe_test", images_ref_list.size.toString())

                        startActivity(intent)
                    } else {
                        updateGallery()
                        Toast.makeText(this@UserProfilePageActivity, "Cette photo n'existe plus :(", Toast.LENGTH_SHORT).show() }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed, how to handle?
                }
            })
        }


    }
    private fun updateGallery() {
        adapter.clear()
        imagelist.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.exists()) {
                    return
                }
                p0.child("pictures").children.forEach { pictures -> // Iterate over pictures
                    val database_ref = pictures.ref.toString()
                    val date_uploaded = pictures.child("date_uploaded").value
                    imagelist[(date_uploaded as Long) * -1] = CloudGridItem(database_ref)
                }
                txt_user_name.text = p0.child("username").value.toString()
                txt_user_name.text = uid

                adapter.update(imagelist.values)
                user_picture_grid_layout.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })
    }
}