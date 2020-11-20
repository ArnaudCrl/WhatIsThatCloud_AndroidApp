package com.arnaudcayrol.WhatIsThatCloud.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.arnaudcayrol.WhatIsThatCloud.fragments.GlobalGaleryFragment
import com.arnaudcayrol.WhatIsThatCloud.fragments.MyGaleryFragment

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                return MyGaleryFragment()
            }
            1 -> {
                return GlobalGaleryFragment()
            }
            else -> return MyGaleryFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}