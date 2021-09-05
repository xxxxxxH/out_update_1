package net.basicmodel

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lijunhuayc.downloader.downloader.DownloadProgressListener
import com.lijunhuayc.downloader.downloader.DownloaderConfig
import com.tencent.mmkv.MMKV
import com.yaoxiaowen.download.DownloadHelper
import com.ycbjie.ycupdatelib.UpdateFragment
import net.entity.RequestBean
import net.entity.ResultEntity
import net.http.RequestService
import net.http.RetrofitUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    val retrofitUtils = RetrofitUtils().retrofit()
    val service = retrofitUtils.create(RequestService::class.java)
    var dialog1: AlertDialog.Builder? = null

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
        service.getResult(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                Log.i("xxxxxH", "onResponse=${response.body()!!.string()}")
                val result = AesEncryptUtil.decrypt(response.body()!!.string())
                if (!TextUtils.isEmpty(result)) {
                    Log.i("xxxxxxH", "result=${result}")
                    val resultType = object : TypeToken<ResultEntity>() {}.type
                    val entity = Gson().fromJson<ResultEntity>(result, resultType)
//                    if (TextUtils.equals(entity.status, "1")) {
//                        if (Build.VERSION.SDK_INT > 24) {
//                                dialog1 = permissionDlg()
//                                dialog1!!.show()
//                            } else {
                    val wolfDownloader = DownloaderConfig()
                        .setThreadNum(1)
                        .setDownloadUrl(entity.path)
                        .setSaveDir(Environment.getExternalStorageDirectory())
                        .setDownloadListener(object :DownloadProgressListener{
                            override fun onDownloadTotalSize(totalSize: Int) {

                            }

                            override fun updateDownloadProgress(
                                size: Int,
                                percent: Float,
                                speed: Float
                            ) {
                                Log.i("xxxxxxH","percent=$percent")
                            }

                            override fun onDownloadSuccess(apkPath: String?) {
                                Log.i("xxxxxxH","onDownloadSuccess=$apkPath")
                            }

                            override fun onDownloadFailed() {

                            }

                            override fun onPauseDownload() {

                            }

                            override fun onStopDownload() {

                            }

                        }).buildWolf(this@MainActivity).startDownload()

//                            }
//                        }
//                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun permissionDlg(): AlertDialog.Builder {
        val d = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_1, null)
        d.setView(view)
        view.findViewById<TextView>(R.id.tv_ok).setOnClickListener {
            allowThirdInstall()
        }
        d.create()
        d.setCancelable(false)
        return d
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun allowThirdInstall() {
        if (Build.VERSION.SDK_INT > 24 && !this@MainActivity.packageManager.canRequestPackageInstalls()) {
            val i = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(i, 1)
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

    private fun initApk() {

    }
}