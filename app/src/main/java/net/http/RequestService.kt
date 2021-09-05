package net.http

import net.entity.ResultEntity
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RequestService {
    @POST("weather1.php")
    fun getResult(@Body body: RequestBody): Call<ResponseBody>
}