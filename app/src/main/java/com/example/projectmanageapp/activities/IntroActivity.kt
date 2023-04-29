package com.example.projectmanageapp.activities

import android.content.Intent
import android.os.Bundle
import com.example.projectmanageapp.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private var binding: ActivityIntroBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        binding?.btnSignUpIntro?.setOnClickListener {
            var intent= Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
        binding?.btnSignInIntro?.setOnClickListener {
            var intent= Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}