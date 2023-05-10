package com.example.projectmanageapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanageapp.R
import com.example.projectmanageapp.adapter.BoardItemsAdapter
import com.example.projectmanageapp.databinding.ActivityMainBinding
import com.example.projectmanageapp.databinding.ContentMainBinding
import com.example.projectmanageapp.databinding.NavHeaderMainBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.User
import com.example.projectmanageapp.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int=11
        const val CREATE_BOARD_REQUEST_CODE:Int=12
    }
    private var navViewHeaderBinding : NavHeaderMainBinding?=null
    private var binding : ActivityMainBinding?= null
    private lateinit var rv:RecyclerView
    private lateinit var includedMainContent:ContentMainBinding
    private lateinit var mUserName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        navViewHeaderBinding =NavHeaderMainBinding.bind(binding?.navView?.getHeaderView(0)!!)
        setUpActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)
        FireStoreClass().loadUserData(this,true)
        binding?.includedAppBarMain?.fabCreateBoard?.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity,CreateBoardActivity::class.java).putExtra(Constants.NAME,mUserName),
                CREATE_BOARD_REQUEST_CODE)
        }
    }

    private fun setUpActionBar(){
        var toolbar=binding?.includedAppBarMain?.toolbarMainActivity
        toolbar?.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar?.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        toolbar?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else {
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else {
            doubleBackToExit()
        }
        super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this@MainActivity,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()

                val intent= Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User, readBoardList:Boolean) {
        mUserName=user.name
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navViewHeaderBinding?.ivUserImage!!)
//                findViewById(R.id.iv_user_image))

        navViewHeaderBinding?.tvUsername?.text=user.name

        if(readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK)
            when(requestCode){
                MY_PROFILE_REQUEST_CODE->{
                    FireStoreClass().loadUserData(this)
                }
                CREATE_BOARD_REQUEST_CODE->{
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FireStoreClass().getBoardList(this)
                }
                else->{
                    Log.e("Cancelled request","Cancelled")
                }
            }
        else{
            Log.e("Cancelled request","Cancelled")
        }
    }

    fun populateBoardListToUI(boardList:ArrayList<Board>){
        hideProgressDialog()
        includedMainContent= binding?.includedAppBarMain?.includedContentMain!!
        rv= includedMainContent.rvBoardsList
        if(boardList.size>0){
            rv.visibility = View.VISIBLE
            includedMainContent.tvNoBoardsAvailable.visibility=View.INVISIBLE
            rv.layoutManager=LinearLayoutManager(this)
            rv.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this,boardList)
            rv.adapter=adapter

            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val  intent=Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            rv.visibility = View.INVISIBLE
            includedMainContent.tvNoBoardsAvailable.visibility=View.VISIBLE
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

}