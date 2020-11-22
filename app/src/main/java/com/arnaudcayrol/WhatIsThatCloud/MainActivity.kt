package com.arnaudcayrol.WhatIsThatCloud

import OnSwipeTouchListener
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import com.arnaudcayrol.WhatIsThatCloud.network.API_obj
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.registration.LoginActivity
import com.arnaudcayrol.WhatIsThatCloud.utils.BitmapManipulation
import com.arnaudcayrol.WhatIsThatCloud.utils.FileManipluation
import com.arnaudcayrol.WhatIsThatCloud.utils.TabsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_observation.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Math.log
import java.lang.Math.max


class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle // For menu
    val current_user = FirebaseAuth.getInstance().currentUser!!
    private val REQUEST_CODE_TAKE_PICTURE = 1
    private val REQUEST_CODE_SELECT_PICTURE = 2
    private lateinit var pictureUri : Uri
    private lateinit var photoFile: File
    private lateinit var photoFile224: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Menu
        if (FirebaseAuth.getInstance().currentUser!!.isAnonymous) {
            nav_view.menu.findItem(R.id.deconnexion).isVisible = false
        } else {
            nav_view.menu.findItem(R.id.connexion).isVisible = false
        }

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.classment ->         {
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${current_user.uid}")
                    ref.child("experience").setValue(ServerValue.increment(100))
                }
                R.id.about -> Toast.makeText(this, "item2", Toast.LENGTH_SHORT).show()
                R.id.deconnexion -> {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                R.id.connexion -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            true
        }

        // Tabs
        val tabAdapter = TabsPagerAdapter(supportFragmentManager, lifecycle, 2)
        tabs_viewpager.adapter = tabAdapter
        tabs_viewpager.isUserInputEnabled = true

        TabLayoutMediator(tab_layout, tabs_viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Mes observations"
                }
                1 -> {
                    tab.text = "Communauté"
                }
            }
            // Change color of the icons
            tab.icon?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(
                    Color.GREEN,
                    BlendModeCompat.SRC_ATOP
                )
        }.attach()

        // New observation button
        layout_new_activity_selector.isVisible = false
        new_observation_button.setOnClickListener() {
            layout_new_activity_selector.isVisible = true
            new_observation_button.isVisible = false
        }

        layout_new_activity_selector.setOnTouchListener(object: OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeBottom() {
                layout_new_activity_selector.isVisible = false
                new_observation_button.isVisible = true
            }
        })


        // Nav Header
        val headerview = nav_view.getHeaderView(0)
        val name_nav_header = headerview.findViewById<TextView>(R.id.name_nav_header)

        val ref = FirebaseDatabase.getInstance().getReference("/users/${current_user.uid}/experience")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val xp = p0.value as Long
                val level = max(log((xp / 100).toDouble()) / log(2.1) + 2, 1.toDouble())
                nav_header_level.text = "Niveau " + level.toInt().toString()
                nav_header_progressBar.progress = ((level - level.toInt()) * 100).toInt()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        name_nav_header.text = current_user.displayName.toString()

        // New Observation
        CoroutineScope(Job() + Dispatchers.Main ).launch {
            try {
                API_obj.retrofitService.wakeupServer().await()
            }
            catch (e: Exception) {
                // This wakes up the server to gain time, so it is supposed to throw an exception if the server is not awake
            }
        }

        btn_camera.setOnClickListener{
            takePicture()
        }

        btn_gallery.setOnClickListener{
            openGalery()
        }

    }

    override fun onRestart() {
        layout_new_activity_selector.isVisible = false
        new_observation_button.isVisible = true
        super.onRestart()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }


    private fun uploadImage() {
        CoroutineScope(Job() + Dispatchers.Main ).launch {

            // Creating the request to the web server, sending a 224x224 px image
            val fileReqBody = RequestBody.create(MediaType.parse("image/*"), photoFile224)
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file",
                photoFile224.name, fileReqBody)
            val getPropertiesDeferred = API_obj.retrofitService.uploadFileAsync(part)

            try {
                // Await the completion of our Retrofit request
                val cloudList : CloudList = getPropertiesDeferred.await()

                // Passing the result to ResultActivity
                val intent = Intent(applicationContext, ResultActivity::class.java)
                intent.putExtra("CloudList", cloudList)
                intent.putExtra("pictureUri", pictureUri)
                startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Failure: ${e.message}.\n" + getString(R.string.bePatient), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = FileManipluation.getPhotoFile(this, "tempPhoto.jpg")

        //TODO Is this the same as pictureUri ?
        val fileProvider = FileProvider.getUriForFile(this, "com.arnaudcayrol.WhatIsThatCloud.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE)
        } else {
            Toast.makeText(this, getString(R.string.UnableToOpenCamera), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGalery() {
        val openGaleryIntent = Intent()
        openGaleryIntent.type = "image/*"
        openGaleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(openGaleryIntent, getString(R.string.SelectImage)), REQUEST_CODE_SELECT_PICTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) { // In this case we get a file and we need to retreive an Uri as well
            pictureUri = Uri.fromFile(photoFile)
            photoFile224 = BitmapManipulation.resizeTo224(this, pictureUri)
            uploadImage()
        }

        if (requestCode == REQUEST_CODE_SELECT_PICTURE && resultCode == Activity.RESULT_OK) { // In this case you get an Uri so we need to save it to a file
            pictureUri = data?.data!!
            photoFile = FileManipluation.saveImageToTempFile(this, pictureUri, "tempPhoto.jpg")
            photoFile224 = BitmapManipulation.resizeTo224(this, pictureUri)
            uploadImage()
        }

        else super.onActivityResult(requestCode, resultCode, data)
    }


}




