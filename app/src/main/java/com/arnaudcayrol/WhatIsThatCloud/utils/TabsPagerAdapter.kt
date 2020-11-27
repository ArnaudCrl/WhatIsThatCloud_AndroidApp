package com.arnaudcayrol.WhatIsThatCloud.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arnaudcayrol.WhatIsThatCloud.fragments.GlobalGalleryFragment
import com.arnaudcayrol.WhatIsThatCloud.fragments.MyGalleryFragment

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyGalleryFragment()
            1 -> GlobalGalleryFragment()
            else -> MyGalleryFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}