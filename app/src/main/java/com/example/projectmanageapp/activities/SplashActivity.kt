package com.example.projectmanageapp.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.projectmanageapp.databinding.ActivitySplashBinding
import com.example.projectmanageapp.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {
    private var binding:ActivitySplashBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val typeFace: Typeface= Typeface.createFromAsset(assets,"carbon bl.ttf")
        binding?.tvAppName?.typeface=typeFace

        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserId= FireStoreClass().getCurrentUserId()
            if(currentUserId.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            } else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2000)

    }
    
    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}