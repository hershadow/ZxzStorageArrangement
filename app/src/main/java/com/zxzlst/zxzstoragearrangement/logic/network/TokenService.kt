package com.zxzlst.zxzstoragearrangement.logic.network

import com.zxzlst.zxzstoragearrangement.logic.model.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TokenService {
    //  先用GET试一下
    @GET("oauth/2.0/token")
    fun searchToken(@Query("grant_type") grant_type :String,
                    @Query("client_id") ak : String,
                    @Query("client_secret") sk : String): Call<TokenResponse>

/*
    @POST("oauth/2.0/token")
    fun searchTocken(@Body data : Data)

 */
}