package net.basicmodel

import android.os.Environment
import com.xxxxxxh.update.BaseApplication
import net.utils.FileUtils
import java.io.File
import java.util.*

class MyApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
    }

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

    override fun getToken(): String {
        var token: String? = null
        if (!File(Environment.getDownloadCacheDirectory().isAbsolute.toString() + File.separator + "a.testupdate.txt").exists()) {
            token = UUID.randomUUID().toString()
            FileUtils.saveFile(token)
        } else {
            token =
                FileUtils.readrFile(Environment.getDownloadCacheDirectory().isAbsolute.toString() + File.separator + "a.testupdate.txt")
        }
        return token!!
    }
}