package com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.gson.annotations.SerializedName
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.model.ResultInfo

class CameraTakingViewModel : ViewModel() {
    val photoNumber = MutableLiveData<Int>()
    fun setPhotoNumber(num : Int){
        photoNumber.value = num
    }
    fun getPhotoNumber():Int{
        return photoNumber.value ?: 0
    }

    //photo的相关LiveData
    val photoList = mutableListOf<List<ResultInfo>>()


    private val inTokenLiveData = MutableLiveData<String>()
    private val filePathLiveData = MutableLiveData<String>()

    val photoLiveData = Transformations.switchMap(filePathLiveData){
        inTokenLiveData.value?.let { it1 -> Repository.searchPhoto(it1, it) }
    }

    fun searchPhoto(token:String , filePath:String){
        inTokenLiveData.value = token
        filePathLiveData.value = filePath
    }

    //Token相关的LiveData
    var token : String = ""

    private val akLiveData = MutableLiveData<String>()
    private val skLiveData = MutableLiveData<String>()

    val tokenLiveData = Transformations.switchMap(akLiveData){
        skLiveData.value?.let { it1 -> Repository.searchToken(it, it1) }
    }

    fun searchToken(ak:String , sk:String){
        akLiveData.value = ak
        skLiveData.value = sk
    }
}
