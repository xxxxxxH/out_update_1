package com.xxxxxxh.update

import android.app.Application

abstract class BaseApplication :Application() {
    override fun onCreate() {
        super.onCreate()
    }

    abstract fun getAppId(): String
    abstract fun getUrl(): String
    abstract fun getAesPassword(): String
    abstract fun getAesHex(): String
    abstract fun getToken():String

}