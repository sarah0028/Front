package com.example.entofu

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.Toast
import com.example.entofu.splashScreen.CreateAnim.createAnimation
import com.example.entofu.splashScreen.OnAnimationListener
import com.example.entofu.splashScreen.StarterAnimation

class StarterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusStyle()
        setContentView(R.layout.activity_starter)
        usingSplashClass()
    }

    private fun usingSplashClass() {
        StarterAnimation(
            resList = getAnimList(),
            onAnimationListener = object : OnAnimationListener {
                override fun onRepeat() {}

                override fun onEnd() {
                    whatToDoNext()
                }

                override fun onStartAnim() {
                }
            }
        ).startSequentialAnimation(view = findViewById(R.id.imageView))
    }

    private fun getAnimList(): ArrayList<Animation> {
        // create list of animations
        val animList: ArrayList<Animation> = ArrayList()

        animList.add(createAnimation(applicationContext, R.anim.no_animation))
        animList.add(createAnimation(applicationContext, R.anim.rotate))
        animList.add(createAnimation(applicationContext, R.anim.zoom_out_fast))
        animList.add(createAnimation(applicationContext, R.anim.fade_in))

        return animList
    }

    private fun setupStatusStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags: Int = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun whatToDoNext() {
        findViewById<ImageView>(R.id.imageView).visibility = View.GONE
        val intent = Intent(this@StarterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}