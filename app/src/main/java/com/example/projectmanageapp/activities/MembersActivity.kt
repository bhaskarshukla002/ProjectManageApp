package com.example.projectmanageapp.activities


import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanageapp.R
import com.example.projectmanageapp.adapter.MemberListItemAdapter
import com.example.projectmanageapp.databinding.ActivityMembersBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.User
import com.example.projectmanageapp.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails: Board
    private lateinit var binding:ActivityMembersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                intent.getParcelableExtra(Constants.BOARD_DETAILS, Board::class.java)!!
            }else{
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
            }
        }
        setUpActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMembersListDetails(this, TODO("create list") )
    }

    fun setUpMemberList(list:ArrayList<User>){
        hideProgressDialog()
        binding.rvMembersList.layoutManager=LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        binding.rvMembersList.adapter=MemberListItemAdapter(this,list)
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarMembersActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=mBoardDetails.name
        binding.toolbarMembersActivity.setNavigationOnClickListener {
            finish()
        }
    }
}