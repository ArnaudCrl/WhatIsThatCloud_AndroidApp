package com.arnaudcayrol.WhatIsThatCloud

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_gallery_focus.*
import kotlinx.android.synthetic.main.activity_new_observation.*
import kotlinx.android.synthetic.main.galery_cloud_grid_item.*
import kotlinx.android.synthetic.main.galery_cloud_grid_item.view.*

class GalleryFocus : AppCompatActivity() {

    lateinit var image_ref : String
    val current_user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_focus)

        image_ref = (intent.getSerializableExtra("picture") as String)
        val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref)
        ref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user_picture = p0.getValue<UserPicture>()!!

                txt_username_prediction.text = user_picture.author.toString() + " pense qu'il s'agit d'un " + user_picture.prediction.toString()
                if (user_picture.fav.containsKey(current_user.uid)) {
                    gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                    gallery_focus_heart.background.setTint(ContextCompat.getColor(this@GalleryFocus, R.color.favorite_red))
                } else {
                    gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
                    gallery_focus_heart.background.setTint(ContextCompat.getColor(this@GalleryFocus, R.color.grey))
                }

                if (user_picture.agree_with_prediction.containsKey(current_user.uid)
                    || user_picture.disagree_with_prediction.containsKey(current_user.uid)
                    || current_user.uid == user_picture.uid) {
                    btn_approve.isVisible = false
                    btn_disapprove.isVisible = false
                }

                Picasso.get().load(user_picture.url.toString()).into(gallery_focus_image_view)
                focus_txt_like_counter.text = user_picture.fav_count.toString()


            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })

        gallery_focus_heart.setOnClickListener(){
            onHeartClicked(FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref))
        }

        btn_approve.setOnClickListener(){
            onAgreeClicked(FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref)) // TODO mettre en variable de classe
        }
        btn_disapprove.setOnClickListener(){
            onDisagreeClicked(FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref)) // TODO mettre en variable de classe
        }
    }

    private fun onHeartClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)

                if (p.fav.containsKey(current_user.uid)) {
                    // Unstar the post and remove self from stars
                    p.fav_count = p.fav_count - 1
                    p.fav.remove(current_user.uid)
                } else {
                    // Star the post and add self to stars
                    p.fav_count = p.fav_count + 1
                    p.fav[current_user.uid] = true
                }

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                // Transaction completed
//                Log.d("star clicked", "postTransaction:onComplete:" + databaseError!!)
            }
        })
    }

    private fun onAgreeClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)

                p.agree_with_prediction[current_user.uid] = true
//                btn_approve.isVisible = false
//                btn_disapprove.isVisible = false
                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {} //TODO figure out what to put here
        })
    }

    private fun onDisagreeClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)

                p.disagree_with_prediction[current_user.uid] = true
//                btn_approve.isVisible = false
//                btn_disapprove.isVisible = false
                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {} //TODO figure out what to put here
        })
    }



    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }
}