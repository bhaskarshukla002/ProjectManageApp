package com.example.projectmanageapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ActivityCreateBoardBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateBoardActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri?=null
    private lateinit var mUserName:String
    private var mBoardImageUrl:String = ""



    private var binding:ActivityCreateBoardBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if(intent.hasExtra(Constants.NAME)){
            mUserName= intent.getStringExtra(Constants.NAME).toString()
        }

        setUpActionBar()

        binding?.ivBoardImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding?.btnCreate?.setOnClickListener{
            if(mSelectedImageFileUri !=null){
                uploadBoardImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }
    private fun createBoard(){
        val assignedUserArrayList:ArrayList<String> =ArrayList()
        assignedUserArrayList.add(getCurrentUserId())

        var board= Board(binding?.etBoardName?.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUserArrayList)
        FireStoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        if(mSelectedImageFileUri!=null){

            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference
                    .child("USER_IMAGE"+
                            System.currentTimeMillis()+"."+Constants.getFileExtension(this,mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot->
                Log.i("Board Image url ",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mBoardImageUrl=uri.toString()

                    createBoard()
                }
            }.addOnFailureListener{
                Toast.makeText(this@CreateBoardActivity,it.message,Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }
        }
    }
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        finish()
    }
    private fun setUpActionBar(){
        setSupportActionBar(binding?.toolbarCreateBoardActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=resources.getString(R.string.create_board_title)
        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener {
            finish()
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
                &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(this,"Oops, you just denied the permission for storage. You can got to settings",
                Toast.LENGTH_LONG).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode== Constants.PICK_IMAGE_REQUEST_CODE&&data!!.data!=null){
            mSelectedImageFileUri=data.data
            try {
                Glide.with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivBoardImage!!)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}