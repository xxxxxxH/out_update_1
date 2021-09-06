package com.xxxxxxh.update

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.lijunhuayc.downloader.downloader.DownloadProgressListener
import com.lijunhuayc.downloader.downloader.DownloaderConfig
import com.tencent.mmkv.MMKV
import com.xxxxxxh.http.RequestService
import net.http.RetrofitUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateManager {
    val retrofitUtils = RetrofitUtils().retrofit()
    val service = retrofitUtils.create(RequestService::class.java)
    var progressBar: ProgressBar? = null
    var dialog1: AlertDialog? = null
    var dialog2: AlertDialog? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: UpdateManager? = null
            get() {
                if (field == null) {
                    field = UpdateManager()
                }
                return field
            }

        @Synchronized
        fun get(): UpdateManager {
            return instance!!
        }
    }


    fun update(requestBean: String, listener: ResponseListener) {
        service.getResult(requestBean).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                listener.onResponse(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun permissionDlg(
        context: Context,
        activity: Activity,
        imgUrl: String,
        content: String
    ): AlertDialog {
        val d = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null)
        val imgView = view.findViewById<ImageView>(R.id.img)
        val desc = view.findViewById<TextView>(R.id.desc)
        d.setView(view)
        view.findViewById<TextView>(R.id.tv_ok).setOnClickListener {
            allowThirdInstall(context, activity)
            d.dismiss()
        }
        Glide.with(context).load(imgUrl).into(imgView)
        desc.text = content
        d.setCancelable(false)
        return d
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun allowThirdInstall(context: Context, activity: Activity) {
        if (Build.VERSION.SDK_INT > 24) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val uri = Uri.parse("package:" + context.packageName)
                val i = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivityForResult(i, 1)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun installApk(context: Context) {
        val path =
            File(Environment.getExternalStorageDirectory().toString() + File.separator)

        if (Build.VERSION.SDK_INT > 24) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.setDataAndType(
                FileProvider.getUriForFile(
                    context,
                    "$context.packageName.fileprovider",
                    path
                ), "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (context.packageManager.queryIntentActivities(intent, 0).size > 0) {
                context.startActivity(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.putExtra("name", "")
            intent.addCategory("android.intent.category.DEFAULT")
            val packageName: String = context.packageName
            val data = FileProvider.getUriForFile(
                context,
                "$packageName.fileprovider",
                File(
                    Environment.getExternalStorageDirectory().toString() + File.separator + "ss.apk"
                )
            )
            intent.setDataAndType(data, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun downloadDlg(context: Context): AlertDialog {
        val dlg = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_2, null)
        dlg.setView(view)
        progressBar = view.findViewById(R.id.progress_bar)
        return dlg
    }

    fun updateDlg(context: Context,text:String,path: String) : AlertDialog{
        val d = AlertDialog.Builder(context).create()
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_3,null)
        d.setView(view)
        val content = view.findViewById<TextView>(R.id.update)
        content.text = text
        view.findViewById<TextView>(R.id.btn).setOnClickListener {
            download(path,context)
            d.dismiss()
        }
        return d
    }

    private fun download(path: String, context: Context) {
        val buildWolf = DownloaderConfig()
            .setThreadNum(1)
            .setDownloadUrl(path)
            .setSaveDir(Environment.getExternalStorageDirectory())
            .setDownloadListener(object : DownloadProgressListener {
                override fun onDownloadTotalSize(totalSize: Int) {

                }

                override fun updateDownloadProgress(
                    size: Int,
                    percent: Float,
                    speed: Float
                ) {
                    Log.i("xxxxxxH", "percent=$percent")
                    if (dialog2 == null) {
                        dialog2 = downloadDlg(context)
                    }
                    dialog2!!.show()
                    progressBar!!.progress = percent.toInt()
                }

                override fun onDownloadSuccess(apkPath: String?) {
                    Log.i("xxxxxxH", "onDownloadSuccess=$apkPath")
                    dialog2?.let {
                        if (dialog2!!.isShowing) {
                            dialog2!!.dismiss()
                        }
                    }
                    installApk(context)
                }

                override fun onDownloadFailed() {
                    Log.i("xxxxxxH", "onDownloadFailed")
                }

                override fun onPauseDownload() {

                }

                override fun onStopDownload() {

                }

            }).buildWolf(context)
        buildWolf.startDownload()
    }

    fun addReceiver(context: Context): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action == Intent.ACTION_PACKAGE_ADDED) {
                    val data = p1.dataString.toString()
                    data.let {
                        Log.e("Install-apk:", it)
                        if (data.contains(context.packageName.toString())) {
                            MMKV.defaultMMKV()!!.encode("state", true)
                        }
                    }

                }
            }

        }
    }
}