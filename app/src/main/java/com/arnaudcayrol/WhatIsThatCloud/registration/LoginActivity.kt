package com.arnaudcayrol.WhatIsThatCloud.registration

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.ChangeUsername.changeUsername
import com.arnaudcayrol.WhatIsThatCloud.MainActivity
import com.arnaudcayrol.WhatIsThatCloud.R
import com.arnaudcayrol.WhatIsThatCloud.utils.User
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity: AppCompatActivity() {

    private val callbackManager  = CallbackManager.Factory.create()
    private val RC_SIGN_IN = 1000
    private var googleSignInClient : GoogleSignInClient? = null
    private lateinit var loading_dialog : AlertDialog
    private var user_is_registerd : Boolean = false
    private var user_is_in_database : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        user_is_registerd = FirebaseAuth.getInstance().currentUser != null
        if (user_is_registerd){
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            Log.d("loginActivity", "user uid : $uid")

            val database_ref = FirebaseDatabase.getInstance().getReference("/users")
            database_ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    user_is_in_database = p0.child("/$uid").exists()
                    Log.d("loginActivity", "user is in database (inside) : $user_is_in_database")
                    if (user_is_registerd && !user_is_in_database) FirebaseAuth.getInstance().signOut()
                }
                override fun onCancelled(p0: DatabaseError) { }
            })
        }


        Log.d("loginActivity", "User is registerd : $user_is_registerd")
        Log.d("loginActivity", "User is in database : $user_is_in_database")


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Loading icon
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.loading)
        loading_dialog = builder.create()

        facebook_login_button_login.setOnClickListener {
            getFacebookAccessToken()
        }

        google_sign_in_button.setOnClickListener {
            googleSignIn()
        }

        anonymous_login_button.setOnClickListener {
            anonymousLogin()
        }


    }

////////////////////////// ANONYMOUS SIGN IN //////////////////////////////////

    private fun anonymousLogin() {
        loading_dialog.show()

        if (user_is_registerd && user_is_in_database) {
            Log.d("loginActivity", "continuing with same anonymous user UID")
            goToMainActivity()
        } else {
            FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("loginActivity", "Created new anonymous user")
                        saveUserToFirebaseDatabase()
                    }
                }
        }
    }

////////////////////////// FACEBOOK SIGN IN //////////////////////////////////

    private fun getFacebookAccessToken(){

        LoginManager.getInstance().loginBehavior = LoginBehavior.WEB_VIEW_ONLY
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))
        LoginManager.getInstance().registerCallback(callbackManager,object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                firebaseAuthWithFacebook(result)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
//                Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun firebaseAuthWithFacebook(result: LoginResult?){

        val credential = FacebookAuthProvider.getCredential(result?.accessToken?.token!!)
        loading_dialog.show()

        if  (user_is_registerd){ // Try to upgrade anonymous user with google / facebook profile
            FirebaseAuth.getInstance().currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val upgraded_user = FirebaseAuth.getInstance().currentUser
                        changeUsername(upgraded_user!!.uid, upgraded_user.displayName.toString())
                        goToMainActivity()
                    }
                }
                ?.addOnFailureListener {
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                goToMainActivity()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
        } else {
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val database_ref = FirebaseDatabase.getInstance().getReference("/users")
                        database_ref.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.child("/${FirebaseAuth.getInstance().currentUser!!.uid}").exists()) goToMainActivity() else saveUserToFirebaseDatabase()
                            }
                            override fun onCancelled(p0: DatabaseError) { }
                        })                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

////////////////////////// GOOGLE SIGN IN //////////////////////////////////

    private fun googleSignIn() {

        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        loading_dialog.show()

        if  (user_is_registerd){ // Try to upgrade anonymous user with google / facebook profile
            FirebaseAuth.getInstance().currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("loginActivity", "upgraded anonymous user with google account")

                        val upgraded_user = FirebaseAuth.getInstance().currentUser
                        changeUsername(upgraded_user!!.uid, upgraded_user.displayName!!)
                        goToMainActivity()
                    }
                }
                ?.addOnFailureListener {
                    Log.d("loginActivity", "anonymous tried to login to already used google account")
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("loginActivity", "anonymous logged in to google account instead")
                                Log.d("loginActivity", "current user = ${FirebaseAuth.getInstance().currentUser?.displayName}")

                                goToMainActivity()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                            Log.d("loginActivity", "anonymous login with google failed")

                        }
                }
        } else {
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        // check if user is in database
                        val database_ref = FirebaseDatabase.getInstance().getReference("/users")
                        database_ref.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.child("/${FirebaseAuth.getInstance().currentUser!!.uid}").exists()) goToMainActivity() else saveUserToFirebaseDatabase()
                            }
                            override fun onCancelled(p0: DatabaseError) { }
                        })
                    }
                }
                .addOnFailureListener {
                    Log.d("loginActivity", "failed to log in with google")

                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // CallbackManager for facebook auth
        callbackManager.onActivityResult(requestCode,resultCode,data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("firebaseAuthWithGoogle", "Google sign in failed : $e")
                // ...
            }
        }
    }

    private fun saveUserToFirebaseDatabase() {
        val firebase_user = FirebaseAuth.getInstance().currentUser

        val user = if (firebase_user!!.isAnonymous){
            User("Anonymous", 0)
        } else {
            User(firebase_user.displayName.toString(), 0)
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/${firebase_user.uid}")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("save user to db", "Finally we saved the user to Firebase Database")
                goToMainActivity()

            }
            .addOnFailureListener {
                Log.d("save user to db", "Failed to set value to database: ${it.message}")
            }
    }

    private fun goToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("EXIT", true)
        loading_dialog.dismiss()
        startActivity(intent)
    }

//    fun printHashKey(pContext: Context) {
//        try {
//            val info: PackageInfo = pContext.getPackageManager()
//                .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                Log.d("hashkey", hashKey)
//            }
//        } catch (e: NoSuchAlgorithmException) {
//        } catch (e: Exception) {
//        }
//    }

}