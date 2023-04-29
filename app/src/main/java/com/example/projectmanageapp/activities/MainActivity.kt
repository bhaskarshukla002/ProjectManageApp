package com.example.projectmanageapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ActivityMainBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.User
import com.example.projectmanageapp.utils.Constants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int=11
//        const val CREATE_BOARD_ACTIVITY:Int=12
    }

    private var binding:ActivityMainBinding?= null

    private lateinit var mUserName:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        FireStoreClass().loadUserData(this)

        findViewById<FloatingActionButton>(R.id.fab_create_board) .setOnClickListener{
            startActivity(Intent(this@MainActivity,CreateBoardActivity::class.java).putExtra(Constants.NAME,mUserName))
        }


    }

    private fun setUpActionBar(){
        var toolbar:Toolbar=findViewById(R.id.toolbar_main_activity)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        toolbar.setNavigationOnClickListener {
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

    fun updateNavigationUserDetails(user: User) {
        mUserName=user.name
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.iv_user_image))

        findViewById<TextView>(R.id.tv_username).text=user.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK)
            when(requestCode){
                MY_PROFILE_REQUEST_CODE->{
                    FireStoreClass().loadUserData(this)
                }
                else->{
                    Log.e("Cancelled request","")
                }
            }
    }

}