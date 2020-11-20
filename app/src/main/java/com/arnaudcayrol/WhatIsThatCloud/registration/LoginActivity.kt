package com.arnaudcayrol.WhatIsThatCloud.registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arnaudcayrol.WhatIsThatCloud.MainActivity
import com.arnaudcayrol.WhatIsThatCloud.NewObservationActivity
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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity: AppCompatActivity() {

    val callbackManager  = CallbackManager.Factory.create()
    val RC_SIGN_IN = 1000
    var googleSignInClient : GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso)



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
        val user = FirebaseAuth.getInstance().currentUser
        if  (user != null){
            goToMainActivity()
        } else {
            FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveUserToFirebaseDatabase()


//                    val user = FirebaseAuth.getInstance().currentUser
//                    Toast.makeText(this, user!!.uid, Toast.LENGTH_SHORT).show()
//                    FirebaseAuth.getInstance().currentUser!!.linkWithCredential(credential)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

////////////////////////// FACEBOOK SIGN IN //////////////////////////////////

    private fun getFacebookAccessToken(){
        LoginManager.getInstance().loginBehavior = LoginBehavior.WEB_VIEW_ONLY
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
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
        val user = FirebaseAuth.getInstance().currentUser
        if  (user != null){ // Try to upgrade anonymous user with google / facebook profile
            FirebaseAuth.getInstance().currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
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
                        saveUserToFirebaseDatabase()
                    }
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
        val user = FirebaseAuth.getInstance().currentUser
        if  (user != null){ // Try to upgrade anonymous user with google / facebook profile
            FirebaseAuth.getInstance().currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToMainActivity()
                    }
                }
                ?.addOnFailureListener {
//                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
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
                        saveUserToFirebaseDatabase()
                    }
                }
                .addOnFailureListener {
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
        val fireabse_user = FirebaseAuth.getInstance().currentUser

        val user = if (fireabse_user!!.isAnonymous){
            User("Anonymous", 0)
        } else {
            User(fireabse_user.displayName.toString(), 0)
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/${fireabse_user.uid}")
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
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
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