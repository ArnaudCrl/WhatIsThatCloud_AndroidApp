package com.arnaudcayrol.WhatIsThatCloud

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arnaudcayrol.WhatIsThatCloud.fragments.GlobalGaleryFragment
import com.arnaudcayrol.WhatIsThatCloud.fragments.MyGaleryFragment
import com.arnaudcayrol.WhatIsThatCloud.registration.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.cloud_list_item.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle // For menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menu
        toggle = ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> Toast.makeText(
                    this, "item1", Toast.LENGTH_SHORT).show()
                R.id.item2 -> Toast.makeText(
                    this, "item2", Toast.LENGTH_SHORT).show()
                R.id.item3 -> Toast.makeText(
                    this, "item3", Toast.LENGTH_SHORT).show()

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
        new_observation_button.setOnClickListener(){
            val intent = Intent(this, NewObservationActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }
}



class CloudGridItem(val url : String, val context: Context): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Picasso.get().load(url).fit().into(viewHolder.itemView.cloud_image)
    }
    override fun getLayout(): Int {
        return R.layout.cloud_list_item
    }
}

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val myGaleryFragment = MyGaleryFragment()
                return myGaleryFragment
            }
            1 -> {
                val globalGaleryFragment = GlobalGaleryFragment()
                return globalGaleryFragment
            }
            else -> return MyGaleryFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}
