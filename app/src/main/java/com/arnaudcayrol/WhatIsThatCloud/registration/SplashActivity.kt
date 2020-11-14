package com.arnaudcayrol.WhatIsThatCloud.registration

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.MainActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.arnaudcayrol.WhatIsThatCloud.R.layout.activity_splash)

// decide here whether to navigate to Login or Main Activity
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) { // User already logged in and therefor has an uid
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