package net.basicmodel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.xxxxxxh.update.BaseActivity

class SplashActivity:BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun startMainActivity() {
       startActivity(Intent(this,MainActivity::class.java))
    }


}