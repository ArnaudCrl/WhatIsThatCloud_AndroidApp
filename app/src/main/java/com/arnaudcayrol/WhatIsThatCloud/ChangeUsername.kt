package com.arnaudcayrol.WhatIsThatCloud

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Matcher
import java.util.regex.Pattern

object ChangeUsername {

    fun changeUsername(uid : String, new_name : String){
        Log.d("loginChangeUSername", "username : $new_name")

        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        ref.child("username").setValue(new_name)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("pictures").children.forEach(){
                    it.child("author").ref.setValue(new_name)
                }
            }

            override fun onCancelled(error: DatabaseError) {  }
        })

    }

    // Function to validate the username
    fun isValidUsername(name: String?): Boolean {

        // Regex to check valid username.
        val regex = "^[A-Za-z]\\w{3,29}$"

        // Compile the ReGex
        val p: Pattern = Pattern.compile(regex)

        // If the username is empty
        // return false
        if (name == null) {
            return false
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        val m: Matcher = p.matcher(name)

        // Return if the username
        // matched the ReGex
        return m.matches()
    }

}