package com.example.projectmanageapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ActivitySignInBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.User
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding?=null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(binding?.root)
        auth=FirebaseAuth.getInstance()
        setUpActionBar()
        binding?.btnSignIn?.setOnClickListener {
            signInUser()
        }
    }

    private fun signInUser(){
        val email: String = binding?.etEmail?.text.toString().trim { it<=' ' }
        val password: String = binding?.etPassword?.text.toString().trim { it<=' ' }

        if (validateForm(email,password)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        hideProgressDialog()
                        // Sign in success, update UI with the signed-in user's information
                        FireStoreClass().loadUserData(this)
//                        val user = auth.currentUser
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                        updateUI(user)
                    } else {
                        hideProgressDialog()
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
//                        updateUI(null)
                    }
                }
        }
    }

    private fun validateForm(email: String,password: String): Boolean {
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter a Email address")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else->{
                true
            }
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        binding?.toolbarSignInActivity?.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

    fun signInSuccess(loggedInUser: User?) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}