package com.arnaudcayrol.WhatIsThatCloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.utils.ExampleGridItem
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_cloud_example_grid.*

class CloudExampleGridActivity : AppCompatActivity() {
    private lateinit var cloud : String
    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud_example_grid)

        cloud = (intent.getSerializableExtra("cloud_type") as String)
//        Log.d("examples", cloud)
        txt_cloud_name.text = cloud.toUpperCase()

        FirebaseStorage.getInstance().getReference("examples/$cloud").listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { _ -> }

                items.forEach { item ->
//                    Log.d("examples", "working")

                    item.downloadUrl.addOnSuccessListener {
                        adapter.add(ExampleGridItem(it.toString()))
                    }
                }
                gexample_grid_layout.adapter = adapter
            }
            .addOnFailureListener {
            }

    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            android.R.id.home -> {
//                finish()
//            }
//        }
//        return super.onContextItemSelected(item)
//    }
}