package com.arnaudcayrol.WhatIsThatCloud.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.arnaudcayrol.WhatIsThatCloud.MainActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : Activity() {

    private var user_is_registerd : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.arnaudcayrol.WhatIsThatCloud.R.layout.activity_splash)

        user_is_registerd = FirebaseAuth.getInstance().currentUser != null

// decide here whether to navigate to Login or Main Activity

        val user = FirebaseAuth.getInstance().currentUser


        if (user_is_registerd && !user!!.isAnonymous) { // User already logged in and is not anonymous
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}