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
        return "testupdate"
    }

    override fun getAppName(): String {
        return ""
    }

    override fun getUrl(): String {
        return "https://recipesbook.online/worldweather361/weather1.php"
    }

    override fun getAesPassword(): String {
        return "Android123456789"
    }

    override fun getAesHex(): String {
        return "987654321diordnA"
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