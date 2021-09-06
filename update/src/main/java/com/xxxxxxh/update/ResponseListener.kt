package com.xxxxxxh.update

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface ResponseListener {
    fun onResponse(response: Response<ResponseBody>)
}