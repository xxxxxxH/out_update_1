package net.basicmodel

import com.xxxxxxh.update.BaseApplication
import java.util.*

class MyApplication:BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        getToken()
    }

    override fun getAppId(): String {
        return "testupdate"
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
        return UUID.randomUUID().toString()
    }
}