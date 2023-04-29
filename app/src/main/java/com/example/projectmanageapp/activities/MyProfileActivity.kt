package com.example.projectmanageapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ActivityMyProfileBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.User
import com.example.projectmanageapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URL

class MyProfileActivity : BaseActivity() {

    private var binding:ActivityMyProfileBinding?=null
    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserDetails:User
    private var mProfileImageUrl: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()

        showProgressDialog("Please Wait")
        FireStoreClass().loadUserData(this)
        binding?.ivProfileUserImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)

            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }
        binding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty()
                &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(this,"Oops, you just denied the permission for storage. You can got to settings",Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK&&requestCode== Constants.PICK_IMAGE_REQUEST_CODE&&data!!.data!=null){
            mSelectedImageFileUri=data.data
            try {

                Glide.with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivProfileUserImage!!)
            }catch (e:Exception){

            }
        }
    }
    fun setUserDataInUI(user: User){

        mUserDetails = user

        Glide.with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.ivProfileUserImage!!)

        binding?.etName?.setText(user.name)
        binding?.etEmail?.setText(user.email)
        if(user.mobile!=0L){
            binding?.etMobile?.setText(user.mobile.toString())
        }
        hideProgressDialog()
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=resources.getString(R.string.my_profile)
        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            finish()
        }
    }
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri!=null){

            val sRef:StorageReference=
                FirebaseStorage.getInstance().reference
                    .child("USER_IMAGE"+
                            System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot->
                Log.i("Firebase Image uri ",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.e("Downloadable Image URL",uri.toString())


                    mProfileImageUrl=uri.toString()

                    updateUserProfileData()
                }
            }.addOnFailureListener{
                Toast.makeText(this@MyProfileActivity,it.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }
    private fun updateUserProfileData(){
        val userHashMap =HashMap<String,Any>()
//        var anyChangeMade=false
        if((mProfileImageUrl?.isNotEmpty() == true) && mProfileImageUrl!! != mUserDetails?.image){
            userHashMap[Constants.IMAGE]=mProfileImageUrl!!
//            anyChangeMade=true
        }
        if(binding?.etName?.text?.toString()!=mUserDetails?.name ){
            userHashMap[Constants.NAME]= binding?.etName?.text?.toString()!!
//            anyChangeMade=true
        }
        if(binding?.etMobile?.text?.toString()!=mUserDetails?.mobile.toString() ){
            userHashMap[Constants.MOBILE]= binding?.etMobile?.text?.toString()?.toLong()!!
//            anyChangeMade=true
        }
        FireStoreClass().updateUserProfileData(this,userHashMap)
    }
    fun profileUpdateSuccess(){
        hideProgressDialog()
    }
    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}