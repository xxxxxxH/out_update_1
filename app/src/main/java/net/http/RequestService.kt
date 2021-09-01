package net.http

import net.entity.ResultEntity
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RequestService {
    @POST("testsecond.php")
    fun getResult(@Body body: RequestBody): Call<ResultEntity>
}