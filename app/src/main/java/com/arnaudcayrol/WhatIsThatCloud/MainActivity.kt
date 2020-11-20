package com.arnaudcayrol.WhatIsThatCloud

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arnaudcayrol.WhatIsThatCloud.fragments.GlobalGaleryFragment
import com.arnaudcayrol.WhatIsThatCloud.fragments.MyGaleryFragment
import com.arnaudcayrol.WhatIsThatCloud.registration.LoginActivity
import com.arnaudcayrol.WhatIsThatCloud.utils.TabsPagerAdapter
import com.arnaudcayrol.WhatIsThatCloud.utils.UserPicture
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.galery_cloud_grid_item.view.*
import kotlinx.android.synthetic.main.nav_header.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle // For menu
    val current_user = FirebaseAuth.getInstance().currentUser!!

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
                    ref.child("experience").setValue(ServerValue.increment(1))
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
        new_observation_button.setOnClickListener() {
            val intent = Intent(this, NewObservationActivity::class.java)
            startActivity(intent)
        }

//        val ref = FirebaseDatabase.getInstance().getReference("/users/${current_user.uid}")
//        ref.child("experience").setValue(ServerValue.increment(1))
//        onStarClicked(ref)



        // Nav Header
        name_nav_header?.text = current_user.displayName.toString()
        val headerview = nav_view.getHeaderView(0)
        val name_nav_header = headerview.findViewById<TextView>(R.id.name_nav_header)
        name_nav_header.text = current_user.displayName.toString()


    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

}




