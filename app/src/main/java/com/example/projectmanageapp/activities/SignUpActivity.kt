package com.example.projectmanageapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ActivitySignUpBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding:ActivitySignUpBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, "you have Successfully registered",Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim { it<=' ' }
        val email: String = binding?.etEmail?.text.toString().trim { it<=' ' }
        val password: String = binding?.etPassword?.text.toString().trim { it<=' ' }

        if(validateForm(name,email,password)){
//            Toast.makeText(this@SignUpActivity,"Now We can register user",Toast.LENGTH_SHORT).show()
            showProgressDialog(resources.getString(R.string.please_wait))
            val mFirebase=FirebaseAuth.getInstance()
            mFirebase.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result?.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid,name,registeredEmail)
                    FireStoreClass().registerUser(this, user)

//                    FirebaseAuth.getInstance().signOut()
//                    finish()
                }       else{
                    Toast.makeText(this,task.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(name: String, email: String,password: String): Boolean {
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                false
            }
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
        setSupportActionBar(binding?.toolbarSignUpActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}

