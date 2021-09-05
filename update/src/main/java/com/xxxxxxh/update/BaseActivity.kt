package com.xxxxxxh.update

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.example.weeboos.permissionlib.PermissionRequest
import com.facebook.applinks.AppLinkData
import com.tencent.mmkv.MMKV
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    protected var appLink: String? = null
    protected var installReferrer: String? = null
    var msgCount = 0

    val permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @SuppressLint("HandlerLeak")
    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    Log.i("xxxxxH", "msgCount=$msgCount")
                    Log.i("xxxxxH", "appLink=$appLink")
                    Log.i("xxxxxH", "installReferrer=$installReferrer")
                    msgCount++
                    if (msgCount == 10) {
                        startMainActivity()
                    } else {
                        if (!TextUtils.isEmpty(appLink) && !TextUtils.isEmpty(installReferrer)) {
                            startMainActivity()
                        } else {
                            val msg = Message()
                            msg.what = 1
                            sendMessageDelayed(msg, 1000)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        MMKV.initialize(this)
        PermissionRequest.getInstance().build(this)
            .requestPermission(object : PermissionRequest.PermissionListener {
                override fun permissionGranted() {
                    if (TextUtils.isEmpty(MMKV.defaultMMKV()!!.decodeString("facebook"))) {
                        getFacebookInfo()
                    } else {
                        appLink = MMKV.defaultMMKV()!!.decodeString("facebook")
                    }

                    if (TextUtils.isEmpty(MMKV.defaultMMKV()!!.decodeString("google"))) {
                        getGoogleInfo()
                    } else {
                        installReferrer = MMKV.defaultMMKV()!!.decodeString("google")
                    }

                    val msg = Message()
                    msg.what = 1
                    mHandler.sendMessageDelayed(msg, 1000)
                }

                override fun permissionDenied(permissions: ArrayList<String>?) {
                    finish()
                }

                override fun permissionNeverAsk(permissions: ArrayList<String>?) {

                }

            }, permission)
    }

    fun getFacebookInfo() {
        AppLinkData.fetchDeferredAppLinkData(this) {
            appLink = it?.targetUri?.toString() ?: "Applink is empty"
            if (!TextUtils.equals(appLink, "Applink is empty")) {
                MMKV.defaultMMKV()!!.encode("facebook", appLink)
            }
        }
    }

    fun getGoogleInfo() {
        val ref = InstallReferrerClient.newBuilder(this).build()
        ref.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                installReferrer =
                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        ref.installReferrer.installReferrer
                    } else {
                        "Referrer is empty"
                    }
                if (!TextUtils.equals(installReferrer, "Referrer is empty")) {
                    MMKV.defaultMMKV()!!.encode("google", installReferrer)
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                installReferrer = "Referrer is empty"
            }

        })
    }


    abstract fun getLayout(): Int

    abstract fun startMainActivity()
}