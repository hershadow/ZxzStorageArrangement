package com.zxzlst.zxzstoragearrangement.logic.model

import com.google.gson.annotations.SerializedName

data class PhotoResponse(val log_id : String , val result_num : String ,val result : List<ResultInfo>)

data class ResultInfo(val score : Float,val root : String, val keyword : String)