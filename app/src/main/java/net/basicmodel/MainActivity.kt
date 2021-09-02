package net.basicmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import net.entity.RequestBean
import net.entity.ResultEntity
import net.http.RequestService
import net.http.RetrofitUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    val retrofitUtils = RetrofitUtils().retrofit()
    val service = retrofitUtils.create(RequestService::class.java)
    var dialog1:AlertDialog.Builder? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val istatus = MMKV.defaultMMKV()!!.decodeBool("isFirst", true)
        val requestBean = RequestBean()
        requestBean.appId = MyApplication().getAppId()
        requestBean.appName = MyApplication().getAppName()
        requestBean.applink = MMKV.defaultMMKV()!!.decodeString("facebook", "AppLink is empty")
        requestBean.ref = MMKV.defaultMMKV()!!.decodeString("google", "Referrer is empty")
        requestBean.token = MyApplication().getToken()
        requestBean.istatus = istatus
        val requestBody = RequestBody.create(
            MediaType.parse("application/json"),
            AesEncryptUtil.encrypt(Gson().toJson(requestBean))
        )
        service.getResult(requestBody).enqueue(object : Callback<ResultEntity> {
            override fun onResponse(call: Call<ResultEntity>, response: Response<ResultEntity>) {
                Log.i("xxxxxH", "onResponse=$response")
                if (this@MainActivity.packageManager.canRequestPackageInstalls()) {

                } else {
                    dialog1 = permissionDlg()
                    dialog1!!.show()
                }
            }

            override fun onFailure(call: Call<ResultEntity>, t: Throwable) {
                Log.i("xxxxxH", "onFailure")
                dialog1 = permissionDlg()
                dialog1!!.show()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {

        }
    }

    fun permissionDlg(): AlertDialog.Builder {
        val d = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_1, null)
        d.setView(view)
        view.findViewById<TextView>(R.id.tv_ok).setOnClickListener {
            allowThirdInstall()
        }
        d.create()
        return d
    }

    private fun allowThirdInstall() {
        if (Build.VERSION.SDK_INT > 24) {
            val i = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(i,1)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra("name", "")
            intent.addCategory("android.intent.category.DEFAULT")
            val packageName: String = this.packageName
            val data = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                File(
                    Environment.getExternalStorageDirectory().toString() + File.separator + "ss.apk"
                )
            )
            intent.setDataAndType(data, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}