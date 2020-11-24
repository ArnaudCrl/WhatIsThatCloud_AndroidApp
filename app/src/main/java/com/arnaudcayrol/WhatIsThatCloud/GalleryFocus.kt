package com.arnaudcayrol.WhatIsThatCloud

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.ExampleGridItem
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_gallery_focus.*
import kotlinx.android.synthetic.main.activity_gallery_focus.xp_gain_animation
import kotlinx.android.synthetic.main.activity_gallery_focus.xp_group
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_observation.*
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.galery_cloud_grid_item.*
import kotlinx.android.synthetic.main.galery_cloud_grid_item.view.*

class GalleryFocus : AppCompatActivity() {

    private lateinit var image_ref : String
    private val current_user = FirebaseAuth.getInstance().currentUser!!
    private var userHasLiked = false
    var user_is_author = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_focus)


        image_ref = (intent.getSerializableExtra("picture") as String)
        val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref)
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user_picture = p0.getValue<UserPicture>()!!

                if (user_picture.fav.containsKey(current_user.uid)) { // If user already liked the photo
                    gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                    gallery_focus_heart.background.setTint(ContextCompat.getColor(this@GalleryFocus, R.color.favorite_red))
                    userHasLiked = true
                } else { // If user didn't already like the photo
                    gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
                    gallery_focus_heart.background.setTint(ContextCompat.getColor(this@GalleryFocus, R.color.grey))
                    userHasLiked = false
                }

                if (user_picture.agree_with_prediction.containsKey(current_user.uid)
                    || user_picture.disagree_with_prediction.containsKey(current_user.uid)
                    || current_user.uid == user_picture.uid) {

                    btn_approve.isVisible = false
                    btn_disapprove.isVisible = false
                }

                if (current_user.uid == user_picture.uid){
                    txt_username_prediction.text = "Vous pensez qu'il s'agit d'un " + user_picture.prediction.toString()
                    user_is_author = true
                    invalidateOptionsMenu()
                } else {
                    txt_username_prediction.text = user_picture.author.toString() + " pense qu'il s'agit d'un " + user_picture.prediction.toString()
                }


                Picasso.get().load(user_picture.url.toString()).into(gallery_focus_image_view)
                focus_txt_like_counter.text = user_picture.fav_count.toString()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })

        xp_group.isVisible = false

        txt_see_examples.setOnClickListener(){
            startExampleGridActivity()
        }

        gallery_focus_heart.setOnClickListener(){
            onHeartClicked(ref)
        }

        btn_approve.setOnClickListener(){
            onAgreeClicked(ref)
        }
        btn_disapprove.setOnClickListener(){
            onDisagreeClicked(ref)
        }
    }

    fun startExampleGridActivity(){

        val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref + "/prediction")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val intent = Intent(this@GalleryFocus, CloudExampleGrid::class.java)
                intent.putExtra("cloud_type", p0.value.toString().toLowerCase())
                startActivity(intent)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_focus_menu, menu)
        val searchItem = menu?.findItem(R.id.gallery_focus_delete)
        searchItem?.isVisible = false
        if (user_is_author){
            searchItem?.isVisible = true
        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.gallery_focus_delete -> {
                val alert = SetupConfirmationDialog()
                alert.show()
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun SetupConfirmationDialog(): AlertDialog {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.Confirm))
        builder.setMessage("Souhaitez-vous vraiment supprimer cette image de la base de donnée ?")

        builder.setPositiveButton(getString(R.string.Yes))
        { dialog, _ ->
            FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref).removeValue().addOnCompleteListener() {
                if(it.isSuccessful){
                    Toast.makeText(this, "Images supprimée", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }

            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.No)
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        return builder.create()
    }

    fun playXPGainAnimation(){
        xp_group.isVisible = true // reset view position
        xp_group.translationY = 0f
        xp_group.alpha = 1f
        xp_group.scaleX = 1f
        xp_group.scaleY = 1f

        xp_group.animate().apply {
            duration = 1000
            translationYBy(-200f)
            scaleX(1.5f)
            scaleY(1.5f)
            interpolator = AccelerateDecelerateInterpolator()
        }.withStartAction() {
            xp_gain_animation.playAnimation()
        }.withEndAction {
            xp_group.isVisible = false
        }.start()
    }

    private fun onHeartClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)

                if (p.fav.containsKey(current_user.uid)) {
                    p.fav_count = p.fav_count - 1
                    p.fav.remove(current_user.uid)
                } else {
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
                if (committed){
                    if (userHasLiked) {
                        gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
                        gallery_focus_heart.background.setTint(ContextCompat.getColor(this@GalleryFocus, R.color.grey))
                        focus_txt_like_counter.text = (focus_txt_like_counter.text.toString().toInt() - 1).toString()
                        userHasLiked = false
                    } else {
                        gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
                        gallery_focus_heart.background.setTint(ContextCompat.getColor(this@GalleryFocus, R.color.favorite_red))
                        focus_txt_like_counter.text = (focus_txt_like_counter.text.toString().toInt() +  1).toString()
                        userHasLiked = true
                    }
                }
            }
        })
    }

    private fun onAgreeClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)

                p.agree_with_prediction[current_user.uid] = true
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (committed){
                    val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref + "/uid")
                    ref.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(p0: DataSnapshot) {
                            val author_uid = p0.value.toString()
                            val author_ref = FirebaseDatabase.getInstance().getReference("/users/${author_uid}")
                            author_ref.child("experience").setValue(ServerValue.increment(20))
                        }
                    })
                    playXPGainAnimation()
                    val current_user_ref = FirebaseDatabase.getInstance().getReference("/users/${current_user?.uid}")
                    current_user_ref.child("experience").setValue(ServerValue.increment(20))
                    btn_approve.isVisible = false
                    btn_disapprove.isVisible = false
                }
            }
        })
    }

    private fun onDisagreeClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)

                p.disagree_with_prediction[current_user.uid] = true
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (committed){
                    playXPGainAnimation()
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${current_user?.uid}")
                    ref.child("experience").setValue(ServerValue.increment(20)) // Gives 20xp for rating an other users prediction
                    btn_approve.isVisible = false
                    btn_disapprove.isVisible = false
                }
            }
        })
    }

}