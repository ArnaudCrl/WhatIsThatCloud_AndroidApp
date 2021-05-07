package com.arnaudcayrol.WhatIsThatCloud

import OnSwipeTouchListener
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.isVisible
import com.arnaudcayrol.WhatIsThatCloud.ChangeUsername.changeUsername
import com.arnaudcayrol.WhatIsThatCloud.ChangeUsername.isValidUsername
import com.arnaudcayrol.WhatIsThatCloud.network.API_obj
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.registration.LoginActivity
import com.arnaudcayrol.WhatIsThatCloud.utils.BitmapManipulation
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.arnaudcayrol.WhatIsThatCloud.utils.FileManipluation
import com.arnaudcayrol.WhatIsThatCloud.utils.TabsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_global_gallery.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln


class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle // For menu
    private lateinit var current_user : FirebaseUser
    private val REQUEST_CODE_TAKE_PICTURE = 63
    private val REQUEST_CODE_SELECT_PICTURE = 1337
    private lateinit var pictureUri : Uri
    private lateinit var photoFile: File
    private lateinit var photoFile224: File
    private  lateinit var loading_dialog : AlertDialog
    private lateinit var tabAdapter : TabsPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        current_user = FirebaseAuth.getInstance().currentUser!!

//        SystemClock.sleep(1000)

        // Menu
        if (FirebaseAuth.getInstance().currentUser!!.isAnonymous) {
            nav_view.menu.findItem(R.id.deconnexion).isVisible = false
//            nav_view.menu.findItem(R.id.change_name).isVisible = false
        } else {
            nav_view.menu.findItem(R.id.connexion).isVisible = false
        }
        nav_view.menu.findItem(R.id.show_tuto).isVisible = false

//        nav_view.menu.findItem(R.id.change_name).isVisible = false

        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        buildMenu()



        // Tabs
        tabAdapter = TabsPagerAdapter(supportFragmentManager, lifecycle, 2)
        tabs_viewpager.adapter = tabAdapter
        tabs_viewpager.isUserInputEnabled = true
        val my_observations = this.getString(R.string.my_observations)
        val communauty = this.getString(R.string.communauty)

        TabLayoutMediator(tab_layout, tabs_viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = my_observations
                }
                1 -> {
                    tab.text = communauty
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
        new_observation_button.setOnClickListener {
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
        UpdateMenuHeader()


        // Loading icon
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.loading)
        loading_dialog = builder.create()


        btn_camera.setOnClickListener{
            takePicture()
        }

        btn_gallery.setOnClickListener{
            openGallery()
        }

        swipe_down_image.setOnClickListener{
            layout_new_activity_selector.isVisible = false
            new_observation_button.isVisible = true
        }

    }


    override fun onRestart() {
        layout_new_activity_selector.isVisible = false
        new_observation_button.isVisible = true
        UpdateMenuHeader()
        super.onRestart()
    }


    private fun buildMenu(){
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.classment -> {
                    val intent = Intent(applicationContext, RankingActivity::class.java)
                    startActivity(intent)
                }
                R.id.user_guide -> {
                    val intent = Intent(applicationContext, UserGuideActivity::class.java)
                    startActivity(intent)                }
                R.id.change_name -> {
                    changeUsernameDialog()
                }
                R.id.deconnexion -> {
                    ConfirmDeconnexionDialog().show()
                }
                R.id.delete_account -> {
                    ConfirmAccountDeleteDialog().show()
                }
                R.id.connexion -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                R.id.show_tuto -> {
                    val prefs : SharedPreferences = getSharedPreferences("com.arnaudcayrol.WhatIsThatCloud.sharedprefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("first_start", true).apply()
                    Toast.makeText(this, "tutoriels activés", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun changeUsernameDialog(){

        val alert = AlertDialog.Builder(this)
        alert.setTitle(this.getString(R.string.change_username))
        alert.setMessage(this.getString(R.string.choose_new_username))

        // Set an EditText view to get user input
        val input = EditText(this)
        alert.setView(input)
        alert.setPositiveButton( this.getString(R.string.validate),
            DialogInterface.OnClickListener { _, _ ->
                val value = input.text.toString()
                if (isValidUsername(value)) {
                    changeUsername(current_user.uid, value)
                    Toast.makeText(applicationContext, this.getString(R.string.username_changed), Toast.LENGTH_LONG).show()

                    return@OnClickListener
                } else {
                    input.setText("")
                    Toast.makeText(applicationContext, this.getString(R.string.wrong_username), Toast.LENGTH_LONG).show()

                }

            })

        alert.setNegativeButton(this.getString(R.string.cancel),
            DialogInterface.OnClickListener { _, _ ->
                return@OnClickListener
            })
        alert.show()

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun UpdateMenuHeader(){

        val headerview = nav_view.getHeaderView(0)
        val name_nav_header = headerview.findViewById<TextView>(R.id.name_nav_header)
        val nav_header_level = headerview.findViewById<TextView>(R.id.nav_header_level)
        val nav_header_progressBar = headerview.findViewById<ProgressBar>(R.id.nav_header_progressBar)

        val ref = FirebaseDatabase.getInstance().getReference("/users/${current_user.uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.exists()) { return }
                name_nav_header.text = p0.child("username").value.toString()

                val xp = p0.child("experience").value as Long
                val level = (ln((xp / 100).toDouble()) / ln(2.1) + 2).coerceAtLeast(1.0)
                val level_string = this@MainActivity.getString(R.string.level)
                nav_header_level.text = level_string + " " + level.toInt().toString()

                when {
                    level.toInt() == 1 -> {
                        nav_header_progressBar.progress = xp.toInt()
                    }
                    level.toInt() == 2 -> {
                        nav_header_progressBar.progress = (((xp.toDouble() - 100) / 120) * 100).toInt()
                    }
                    else -> {
                        nav_header_progressBar.progress = ((level - level.toInt()) * 100).toInt()
                    }
                }

            }
            override fun onCancelled(p0: DatabaseError) {  }
        })
    }


    private fun uploadImage() {
        loading_dialog.show()

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
                loading_dialog.dismiss()


            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Failure: ${e.message}.\n" + getString(R.string.bePatient), Toast.LENGTH_LONG).show()
            }
        }

    }


    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = FileManipluation.getPhotoFile(this, SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()))

        //TODO Is this the same as pictureUri ?
        val photoUri = FileProvider.getUriForFile(this, "com.arnaudcayrol.WhatIsThatCloud.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE)
        } else {
            Toast.makeText(this, getString(R.string.UnableToOpenCamera), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
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

    private fun ConfirmDeconnexionDialog(): AlertDialog{

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.Confirm))
        builder.setMessage(this.getString(R.string.wish_to_disconnect))
        builder.setPositiveButton(getString(R.string.Yes))
        { dialog, _ ->
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, this.getString(R.string.signed_out), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("EXIT", true)    //            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.No)
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        return builder.create()
    }

    private fun ConfirmAccountDeleteDialog(): AlertDialog{

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.Confirm))
        builder.setMessage(this.getString(R.string.confirm_delete_account))

        builder.setPositiveButton(getString(R.string.Yes))
        { dialog, _ ->
            finish()
            val ref = FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().currentUser!!.uid}")
            ref.removeValue().addOnSuccessListener {
                Log.d("delete user", "database entry removed")
                FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener {
                    Log.d("delete user", "user deleted")
                    Toast.makeText(this, this.getString(R.string.account_deleted), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("EXIT", true)    //            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    dialog.dismiss()
                }
            }
        }

        builder.setNegativeButton(getString(R.string.No)
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        return builder.create()
    }


//    private fun save_last_displayed_tab(){
//        val tab = tabAdapter.getc
//        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
//        with (sharedPref.edit()) {
//            putInt(getString(R.string.main_actyvity_last_opened_tab), tab.toInt())
//            apply()
//        }
//    }


}




