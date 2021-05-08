package com.arnaudcayrol.WhatIsThatCloud

import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.arnaudcayrol.WhatIsThatCloud.utils.ColorUtils
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.gallery_focus_item.view.*


class SwipeGalleryViewPagerAdapter(private var image_refs : ArrayList<String>) : RecyclerView.Adapter<SwipeGalleryViewPagerAdapter.ViewPagerVieHolder>() {

    private val current_user = FirebaseAuth.getInstance().currentUser!!

    inner class ViewPagerVieHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val xp_group : ConstraintLayout = itemView.findViewById(R.id.xp_group)
        val xp_gain_animation : LottieAnimationView = itemView.findViewById(R.id.xp_gain_animation)
        val btn_approve : Button = itemView.findViewById(R.id.btn_approve)
        val btn_disapprove : Button = itemView.findViewById(R.id.btn_disapprove)


        fun startExampleGridActivity(image_ref : String){
            val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref + "/prediction")
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if(!p0.exists()) { return }
                    val intent = Intent(itemView.context, CloudExampleGridActivity::class.java)
                    intent.putExtra("cloud_type", p0.value.toString().toLowerCase())
                    startActivity(itemView.context, intent, null)
                }
            })

        }

        fun startUserProfilePageActivity(image_ref : String){
            val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_ref + "/uid")
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if(!p0.exists()) { return }
                    val intent = Intent(itemView.context, UserProfilePageActivity::class.java)
                    intent.putExtra("uid", p0.value.toString())
                    startActivity(itemView.context, intent, null)
                }
            })
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
            }.withStartAction {
                xp_gain_animation.playAnimation()
            }.withEndAction {
                xp_group.isVisible = false
            }.start()
        }

//        fun onHeartClicked(postRef: DatabaseReference) {
//            postRef.runTransaction(object : Transaction.Handler {
//                override fun doTransaction(mutableData: MutableData): Transaction.Result {
//                    val p = mutableData.getValue(UserPicture::class.java) ?: return Transaction.success(mutableData)
//
//                    if (p.fav.containsKey(current_user.uid)) {
//                        userHasLiked = true
//                        p.fav_count = p.fav_count - 1
//                        p.fav.remove(current_user.uid)
//                    } else {
//                        userHasLiked = false
//                        p.fav_count = p.fav_count + 1
//                        p.fav[current_user.uid] = true
//                    }
//
//                    // Set value and report transaction success
//                    mutableData.value = p
//                    return Transaction.success(mutableData)
//                }
//
//                override fun onComplete(
//                    databaseError: DatabaseError?,
//                    committed: Boolean,
//                    currentData: DataSnapshot?
//                ) {
//                    if (committed){
//                        if (userHasLiked) {
//                            gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
//                            gallery_focus_heart.background.setTint(ContextCompat.getColor(itemView.context, R.color.grey))
//                            focus_txt_like_counter.text = (focus_txt_like_counter.text.toString().toInt() - 1).toString()
//                            userHasLiked = false
//                        } else {
//                            gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
//                            gallery_focus_heart.background.setTint(ContextCompat.getColor(itemView.context, R.color.favorite_red))
//                            focus_txt_like_counter.text = (focus_txt_like_counter.text.toString().toInt() +  1).toString()
//                            userHasLiked = true
//                        }
//                    }
//                }
//            })
//        }

        fun onAgreeClicked(postRef: DatabaseReference, image_ref : String) {
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
                                if(!p0.exists()) { return }
                                val author_uid = p0.value.toString()
                                val author_ref = FirebaseDatabase.getInstance().getReference("/users/${author_uid}")
                                author_ref.child("experience").setValue(ServerValue.increment(20))
                            }
                        })
                        playXPGainAnimation()
                        val current_user_ref = FirebaseDatabase.getInstance().getReference("/users/${current_user.uid}")
                        current_user_ref.child("experience").setValue(ServerValue.increment(20))
                        btn_approve.isVisible = false
                        btn_disapprove.isVisible = false
                    }
                }
            })
        }

        fun onDisagreeClicked(postRef: DatabaseReference) {
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
                        val ref = FirebaseDatabase.getInstance().getReference("/users/${current_user.uid}")
                        ref.child("experience").setValue(ServerValue.increment(20)) // Gives 20xp for rating an other users prediction
                        btn_approve.isVisible = false
                        btn_disapprove.isVisible = false
                    }
                }
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerVieHolder {
        return ViewPagerVieHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_focus_item, parent, false))
    }

    override fun getItemCount(): Int {
        return image_refs.size
    }

    override fun onBindViewHolder(holder: ViewPagerVieHolder, position: Int) {
//        Log.d("swipe_test", "onBindViewHolder")

        val ref = FirebaseDatabase.getInstance().getReferenceFromUrl(image_refs[position])
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if(!p0.exists()) { return }
                val user_picture = p0.getValue<UserPicture>()!!

//                if (user_picture.fav.containsKey(current_user.uid)) { // If user already liked the photo
//                    holder.itemView.gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
//                    holder.itemView.gallery_focus_heart.background.setTint(ContextCompat.getColor(holder.itemView.context, R.color.favorite_red))
//                    userHasLiked = true
//                } else { // If user didn't already like the photo
//                    holder.itemView.gallery_focus_heart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
//                    holder.itemView.gallery_focus_heart.background.setTint(ContextCompat.getColor(holder.itemView.context, R.color.grey))
//                    userHasLiked = false
//                }

                if (user_picture.agree_with_prediction.containsKey(current_user.uid)
                    || user_picture.disagree_with_prediction.containsKey(current_user.uid)
                    || current_user.uid == user_picture.uid) {

                    holder.itemView.btn_approve.isVisible = false
                    holder.itemView.btn_disapprove.isVisible = false
                }

                if (current_user.uid == user_picture.uid){
                    val vous_pensez = holder.itemView.context.getString(R.string.vous_pensez)
                    holder.itemView.txt_username_prediction.text = vous_pensez + " " + user_picture.prediction.toString()
                } else {


                    val xxx_pense_que = holder.itemView.context.getString(R.string.xxx_pense_que)
                    val username = user_picture.author.toString()
                    val result = TextUtils.concat(" ",xxx_pense_que," ",user_picture.prediction.toString())


                    holder.itemView.txt_username_prediction.setText(username + result, TextView.BufferType.SPANNABLE)

                    val span = holder.itemView.txt_username_prediction.text as Spannable
                    span.setSpan(ForegroundColorSpan(Color.BLUE), 0, username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                    holder.itemView.txt_username_prediction.text = TextUtils.concat(username, result)
                }


                Picasso.get().load(user_picture.url.toString()).into(holder.itemView.gallery_focus_image_view)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("fetchUsers", "error : $p0")
            }
        })

        holder.itemView.xp_group.isVisible = false

        holder.itemView.txt_see_examples.setOnClickListener {
            holder.startExampleGridActivity(image_refs[position])
        }
        holder.itemView.txt_username_prediction.setOnClickListener {
            holder.startUserProfilePageActivity(image_refs[position])
        }

        holder.itemView.btn_approve.setOnClickListener {
            holder.onAgreeClicked(ref, image_refs[position])
        }
        holder.itemView.btn_disapprove.setOnClickListener {
            holder.onDisagreeClicked(ref)
        }
    }
}

