package com.arnaudcayrol.WhatIsThatCloud

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.ColorUtils
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.example_cloud_list_item.view.*

import java.util.*
import kotlin.collections.HashMap

class ResultActivity : AppCompatActivity() {

    private lateinit var pictureUri : Uri
    private lateinit var cloudList: CloudList // Ordered pairs of (cloud name , cloud proba ) ordered by probability
    private var pageNumber : Int = 0
    private var feedbackSent : Boolean = false
    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview
    private lateinit var urlList : Map<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        cloudList = (intent.getSerializableExtra("CloudList") as? CloudList)!!
        pictureUri = intent.getParcelableExtra("pictureUri") as Uri

        Picasso.get().load(pictureUri).into(picUserPicture)

        btn_left_arrow.isEnabled = false
        btn_left_arrow.setImageResource(R.drawable.left_arrow_off)
        ColorUtils.writeColoredResultText(this, txt_result, cloudList.resultList[0])

        updateRecyclerView(cloudList.resultList[0].first)

        val alert = SetupConfirmationDialog()


        btn_left_arrow.setOnClickListener {
            goToPreviousPrediction()
        }
        btn_right_arrow.setOnClickListener {
            goToNextPrediction()
        }
        btn_validationfeedback.setOnClickListener {
            alert.show()
        }
        btn_wikiRedirect.setOnClickListener {
            GoToURL(urlList[cloudList.resultList[pageNumber].first])
        }

        urlList= mapOf(
            "Altocumulus" to getString(R.string.wikiAltocumulus),
            "Altostratus" to getString(R.string.wikiAltostratus),
            "Cirrocumulus" to getString(R.string.wikiCirrocumulus),
            "Cirrostratus" to getString(R.string.wikiCirrostratus),
            "Cirrus" to getString(R.string.wikiCirrus),
            "Cumulonimbus" to getString(R.string.wikiCumulonimbus),
            "Cumulus" to getString(R.string.wikiCumulus),
            "Nimbostratus" to getString(R.string.wikiNimbostratus),
            "Stratocumulus" to getString(R.string.wikiStratocumulus),
            "Stratus" to getString(R.string.wikiStratus)
        )
    }

    private fun updateRecyclerView(cloud_type: String) {
        adapter.clear()
        for (x in 1..9) {
            adapter.add(CloudItem(cloud_type, x, this))
        }
        recyclerViewClouds.adapter = adapter
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

    private fun SetupConfirmationDialog(): AlertDialog{

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.Confirm))
        builder.setMessage(getString(R.string.IsThisResultCorrect))

        builder.setPositiveButton(getString(R.string.Yes))
        { dialog, _ ->
            btn_validationfeedback.isEnabled = false
            btn_validationfeedback.setImageResource(R.drawable.validation_icon_clicked)
            Handler().postDelayed({Toast.makeText(this, getString(R.string.ThankYouForYourFeedback), Toast.LENGTH_LONG).show()}, 1000)

            addFeedbackToFirabaseDatabase()
            onBackPressed()

            feedbackSent = true
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.No)
        ) { dialog, _ -> // Do nothing
            dialog.dismiss()
        }

        return builder.create()
    }


    private fun goToNextPrediction() {

        if (pageNumber == 0){
            btn_left_arrow.isEnabled = true
            btn_left_arrow.setImageResource(R.drawable.left_arrow_on)
        }
        if (pageNumber == cloudList.resultList.size - 2) {
            btn_right_arrow.isEnabled = false
            btn_right_arrow.setImageResource(R.drawable.right_arrow_off)
        }
        pageNumber++
        ColorUtils.writeColoredResultText(this, txt_result, cloudList.resultList[pageNumber])
        updateRecyclerView(cloudList.resultList[pageNumber].first)

    }

    private fun goToPreviousPrediction() {
        if (pageNumber == cloudList.resultList.size - 1){
            btn_right_arrow.isEnabled = true
            btn_right_arrow.setImageResource(R.drawable.right_arrow_on)
        }
        if (pageNumber == 1) {
            btn_left_arrow.isEnabled = false
            btn_left_arrow.setImageResource(R.drawable.left_arrow_off)
        }
        pageNumber--
        ColorUtils.writeColoredResultText(this, txt_result, cloudList.resultList[pageNumber])
        updateRecyclerView(cloudList.resultList[pageNumber].first)

    }


    private fun addFeedbackToFirabaseDatabase() {

        val user = FirebaseAuth.getInstance().currentUser

        // Add to Firebase Storage
        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/images/$filename")

        storageRef.putFile(pictureUri)
            .addOnSuccessListener {
                Log.d("firebaseDatabase", "Successfully uploaded image: ${it.metadata?.path}")
                storageRef.downloadUrl.addOnSuccessListener {

                    // Add to Firebase Database
                    val userPicture = UserPicture(user?.uid.toString(), it.toString(), user?.displayName.toString() ,cloudList.resultList[pageNumber].first)

                    val databaseRef = FirebaseDatabase.getInstance().getReference("/users/${user?.uid}/pictures/$filename")
                    databaseRef.setValue(userPicture)
                        .addOnSuccessListener {
                            Log.d("firebaseDatabase", "Successfully added image to database")
                        }
                        .addOnFailureListener {
                            Log.d("firebaseDatabase", "Failed to set value to database: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                Log.d("firebaseDatabase", "Failed to upload image to storage: ${it.message}")
            }
    }



    fun GoToURL(url: String?) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }



}

class CloudItem(val cloudType : String, val cloudIndex : Int, val context: Context): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val name = (cloudType + "_" + cloudIndex).toLowerCase(Locale.ROOT)
        val id: Int = context.resources.getIdentifier(name, "drawable", context.packageName)
        Picasso.get().load(id).into(viewHolder.itemView.cloud_image)
    }

    override fun getLayout(): Int {
        return R.layout.example_cloud_list_item
    }
}




