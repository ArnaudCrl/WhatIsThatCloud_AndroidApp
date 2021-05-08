package com.arnaudcayrol.WhatIsThatCloud

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arnaudcayrol.WhatIsThatCloud.utils.CloudGridItem
import com.arnaudcayrol.WhatIsThatCloud.utils.RankingItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_ranking.*

class RankingActivity : AppCompatActivity() {

    private val map = sortedMapOf<String, Pair<String, Long> >()
    private val adapter = GroupAdapter<ViewHolder>() // For recyclerview


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) { return }
                snapshot.children.forEach { // Probably dumb to use a map instead of a list idk
                    map[it.key.toString()] = Pair( it.key.toString() ,it.child("experience").value as Long) //{ uid : experience }
                }
                val result = map.toList().sortedBy { (_, value) -> value.second}.toMap()
                result.forEach {
                    if (it.value.first != "Anonymous" && it.value.first != "" && it.value.second > 0)  {
                        adapter.add(0, RankingItem(it.value.first, it.value.second.toString()))
                    }
                }
                rank_recycler_view.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {  }
        })

        adapter.setOnItemClickListener { item, _ ->
            val user = item as RankingItem
            val intent = Intent(this@RankingActivity, UserProfilePageActivity::class.java)
            intent.putExtra("uid", user.uid_)
            ContextCompat.startActivity(this@RankingActivity, intent, null)
        }
    }


}