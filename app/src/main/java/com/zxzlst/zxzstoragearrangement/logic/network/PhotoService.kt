package com.zxzlst.zxzstoragearrangement.logic.network


import com.zxzlst.zxzstoragearrangement.logic.model.PhotoResponse
import retrofit2.Call
import retrofit2.http.*

interface PhotoService {
    @Headers("Content-Type:application/x-www-form-urlencoded")
    @POST("rest/2.0/image-classify/v2/advanced_general")
    fun searchPhoto(@Query("access_token")token : String,
                    @Body photo : String):Call<PhotoResponse>
}