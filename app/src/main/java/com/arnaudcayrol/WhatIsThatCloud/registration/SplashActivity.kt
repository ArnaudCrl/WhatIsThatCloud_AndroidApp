package com.arnaudcayrol.WhatIsThatCloud.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.arnaudcayrol.WhatIsThatCloud.MainActivity
import com.arnaudcayrol.WhatIsThatCloud.NewObservationActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.arnaudcayrol.WhatIsThatCloud.R.layout.activity_splash)

// decide here whether to navigate to Login or Main Activity
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && !user.isAnonymous) { // User already logged in and is not anonymous
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