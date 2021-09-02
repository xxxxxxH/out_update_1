package net.basicmodel

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
    val retrofitUtils = RetrofitUtils().retrofit()
    val service = retrofitUtils.create(RequestService::class.java)
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
            }

            override fun onFailure(call: Call<ResultEntity>, t: Throwable) {
                Log.i("xxxxxH", "onFailure")
            }

        })
    }
}