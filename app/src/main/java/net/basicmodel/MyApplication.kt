package net.basicmodel

import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.xxxxxxh.update.BaseApplication
import net.utils.FileUtils
import java.io.File
import java.util.*

class MyApplication : BaseApplication() {

    override fun getAppId(): String {
        return "361"
    }

    override fun getAppName(): String {
        return "net.basicmodel"
    }

    override fun getUrl(): String {
        return "http://smallfun.xyz/worldweather361/"
    }

    override fun getAesPassword(): String {
        return "VPWaTtwYVPS1PeQP"
    }

    override fun getAesHex(): String {
        return "jQ4GbGckQ9G7ACZv"
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun getToken(): String {
        var token: String? = null
        if (!File(Environment.getExternalStorageDirectory().toString() + File.separator + "a.testupdate.txt").exists()) {
            token = UUID.randomUUID().toString()
            FileUtils.saveFile(token)
        } else {
            token =
                FileUtils.readrFile(Environment.getExternalStorageDirectory().toString() + File.separator + "a.testupdate.txt")
        }
        return token!!
    }
}