package com.zxzlst.zxzstoragearrangement.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
retrofit发送封装单例
 */

object HomeNetwork {
    //封装TOKEN接口
    private val tokenService = ServiceCreator.create(TokenService::class.java)
    suspend fun searchToken(ak:String , sk:String) = tokenService.searchToken("client_credentials",ak,sk).await()

    //封装PHOTO接口
    private val photoService = ServiceCreator.create(PhotoService::class.java)
    suspend fun searchPhoto(token:String , photo:String) = photoService.searchPhoto(token,photo).await()

    //await工具类
    private suspend fun <T> Call<T>.await() :T{
        return suspendCoroutine {
                continuation -> enqueue(object : Callback<T>{
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body!=null) continuation.resume(body)
                else continuation.resumeWithException(RuntimeException("response body is null"))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
        }
    }
}