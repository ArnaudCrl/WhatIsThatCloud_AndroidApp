package com.arnaudcayrol.WhatIsThatCloud

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_gallery_swipe.*


class GallerySwipeActivity : AppCompatActivity() {

    private lateinit var image_refs : ArrayList<String>
    private lateinit var current_ref : String
    private var user_is_author = false
    private val current_user = FirebaseAuth.getInstance().currentUser!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_swipe)

        image_refs = intent.getSerializableExtra("pictures_ref") as ArrayList<String>
        current_ref = intent.getSerializableExtra("current_ref") as String
        val index = image_refs.indexOf(current_ref)

        val adapter = SwipeGalleryViewPagerAdapter(image_refs)
        pager.adapter = adapter
        pager.currentItem = index

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                check_if_user_is_author()
            }
        })

    }

    fun check_if_user_is_author(){
        val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_refs[pager.currentItem])
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.exists()) { return }
                val author_uid = p0.child("uid").value.toString()
                user_is_author = current_user.uid == author_uid || current_user.uid == "DG8GLZQbufMkAuGM7DW8ctJxo3d2" // My uid
                invalidateOptionsMenu()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_focus_menu, menu)
        val searchItem = menu?.findItem(R.id.gallery_focus_delete)
        searchItem?.isVisible = false
        if (user_is_author){
            searchItem?.isVisible = true
        } //else {searchItem?.isVisible = true} //TODO : remove that for relase

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
        builder.setMessage(getString(R.string.confirm_delete_pic))

        builder.setPositiveButton(getString(R.string.Yes))
        { dialog, _ ->
            FirebaseDatabase.getInstance().getReferenceFromUrl(current_ref).removeValue().addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, getString(R.string.image_deleted), Toast.LENGTH_SHORT).show()
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

}