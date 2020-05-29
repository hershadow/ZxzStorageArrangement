package com.zxzlst.zxzstoragearrangement.logic.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(val refresh_token : String,
                         var expires_in : String,
                         @SerializedName("formatted_session_key")val session_key : String,
                         val access_token : String,
                         @SerializedName("formatted_scope")val scope : String,
                         val session_secret : String)