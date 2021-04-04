package com.dovoo.memesnetwork.network

import com.dovoo.memesnetwork.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object MemesRestAdapter {
    private fun getRestService(url: String): MemesRestService {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            //.client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MemesRestService::class.java)
    }

    val apiRestService: MemesRestService by lazy {
        getRestService(BuildConfig.API_URL)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES)
        builder.followRedirects(false)

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

//        try {
//            val pandora = Class.forName("tech.linjiang.pandora.Pandora")
//            val mGet = pandora.getMethod("get")
//            val mGetObj = mGet.invoke(null)
//            val mGetInterceptor = pandora.getMethod("getInterceptor")
//            val interceptor = mGetInterceptor.invoke(mGetObj)
//            builder.addInterceptor(interceptor as Interceptor)
//        } catch (e: Exception) {
//        } catch (e: Throwable) {
//        }

        return builder.build()
    }
}