package com.arnaudcayrol.WhatIsThatCloud

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.arnaudcayrol.WhatIsThatCloud.network.CloudList
import com.arnaudcayrol.WhatIsThatCloud.utils.BitmapManipulation.resizeTo1080p
import com.arnaudcayrol.WhatIsThatCloud.utils.ColorUtils
import com.arnaudcayrol.WhatIsThatCloud.utils.ExampleResultItem
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_result.*
import java.util.*
import kotlin.math.ln

class ResultActivity : AppCompatActivity() {

    private lateinit var pictureUri : Uri
    private lateinit var cloudList: CloudList // Ordered pairs of (cloud name , cloud proba ) ordered by probability
    private var pageNumber : Int = 0
    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview
    private lateinit var urlList : Map<String, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        cloudList = (intent.getSerializableExtra("CloudList") as? CloudList)!!
        pictureUri = intent.getParcelableExtra("pictureUri") as Uri

        Picasso.get().load(pictureUri).into(picUserPicture)
        xp_group.isVisible = false
        btn_left_arrow.isEnabled = false
        btn_left_arrow.setImageResource(R.drawable.left_arrow_off)
        ColorUtils.writeColoredResultText(this, txt_result, cloudList.resultList[0])

        updateRecyclerView(cloudList.resultList[0].first)

        xp_group.isVisible = false
        val alert = SetupConfirmationDialog()


        // First connexion
        val prefs : SharedPreferences = getSharedPreferences("com.arnaudcayrol.WhatIsThatCloud.sharedprefs", Context.MODE_PRIVATE)

        val first_start = prefs.getBoolean("first_start", true)

        Log.d("shared prefs", first_start.toString())
//        prefs.edit().putBoolean("first_start", true).apply()


        val tapTargetSequence = TapTargetSequence(this)
            .targets(
                TapTarget.forView(txt_result, "La prédiction de l'IA", "Ici vous pouvez lire le nom  du/des nuages choisi par l'IA avec son degré de confiance associé")
                    .outerCircleColor(R.color.blue_green)      // Specify a color for the outer circle
                    .outerCircleAlpha(0.90f)            // Specify the alpha amount for the outer circle
                    .targetCircleColor(R.color.white)   // Specify a color for the target circle
                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
                    .titleTextColor(R.color.white)      // Specify the color of the title text
                    .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                    .descriptionTextColor(R.color.white)  // Specify the color of the description text
                    .transparentTarget(false)
                    .targetRadius(120),

                TapTarget.forView(btn_validationfeedback, "Validez l'identification", "Si le résultat vous semble correct, validez le afin de renforcer la précision de l'IA. \n\nVous pouvez voir d'autres propositions à l'aide des flèches si celle-ci ne correspond pas.\n\nValider un résultat vous rapporte des points d'experience !")
                    .outerCircleColor(R.color.blue_green)      // Specify a color for the outer circle
                    .outerCircleAlpha(0.90f)            // Specify the alpha amount for the outer circle
                    .targetCircleColor(R.color.sky_blue)   // Specify a color for the target circle
                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
                    .titleTextColor(R.color.white)      // Specify the color of the title text
                    .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                    .descriptionTextColor(R.color.white)  // Specify the color of the description text
                    .transparentTarget(true))

            .continueOnCancel(true)

            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceCanceled(lastTarget: TapTarget?) { }
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) { }
                override fun onSequenceFinish() { }
            })

        if (first_start){
            tapTargetSequence.start()
            prefs.edit().putBoolean("first_start", false).apply()

        }

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

        FirebaseStorage.getInstance().getReference("examples/${cloud_type.toLowerCase(Locale.ROOT)}")
            .listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { _ ->
                }
                adapter.clear()
                items.forEach { item ->
                    item.downloadUrl.addOnSuccessListener {
                        adapter.add(ExampleResultItem(it.toString()))
                        Log.d("examples", "added one url")

                    }
                }

                recyclerViewClouds.adapter = adapter
            }

            .addOnFailureListener {
                Log.d("examples", it.toString())
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

        storageRef.putFile(Uri.fromFile(resizeTo1080p(this, pictureUri)))
            .addOnSuccessListener {
                Log.d("firebaseDatabase", "Successfully uploaded image: ${it.metadata?.path}")
                storageRef.downloadUrl.addOnSuccessListener {url ->

                    // Add to Firebase Database
                    val ref = FirebaseDatabase.getInstance().getReference("/users/${user?.uid}")
                    ref.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists()) { return }
                            val username = if (user!!.isAnonymous) "Anonymous" else p0.child("username").value as String
                            val databaseRef = FirebaseDatabase.getInstance().getReference("/users/${user.uid}/pictures/$filename")
                            val userPicture = UserPicture(user.uid, url.toString(), username, cloudList.resultList[pageNumber].first)
                            databaseRef.setValue(userPicture)
                                .addOnSuccessListener {
                                    ref.child("experience").setValue(ServerValue.increment(100)) // Gives 100 xp to user for submitting an image
                                    playXPGainAnimation()
                                    Log.d("firebaseDatabase", "Successfully added image to database")
                                }
                                .addOnFailureListener {exception ->
                                    Log.d("firebaseDatabase", "Failed to set value to database: ${exception.message}")
                                }
                        }
                        override fun onCancelled(p0: DatabaseError) {  }
                    })

                }
            }
            .addOnFailureListener {
                Log.d("firebaseDatabase", "Failed to upload image to storage: ${it.message}")
            }
    }



    private fun GoToURL(url: String?) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


    private fun playXPGainAnimation(){
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
        }.withStartAction {
            xp_gain_animation.playAnimation()
        }.withEndAction {
            xp_group.isVisible = false
            onBackPressed()
        }.start()
    }

}




