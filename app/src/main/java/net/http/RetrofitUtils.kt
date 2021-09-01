package net.http

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitUtils {
    companion object {
        private var instance: RetrofitUtils? = null
            get() {
                if (field == null) {
                    field = RetrofitUtils()
                }
                return field
            }

        @Synchronized
        fun get(): RetrofitUtils {
            return instance!!
        }
    }


    fun retrofit(): Retrofit {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
            val url = request.url().toString()
            chain.proceed(request)
        };
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
        val client = builder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(NetConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        return retrofit
    }
}