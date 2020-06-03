package com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.zxzlst.zxzstoragearrangement.ChipOptions
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import com.zxzlst.zxzstoragearrangement.logic.model.ResultInfo
import java.io.File

class CameraInsertViewModel : ViewModel() {
    // insertItemList 保存输入文本框信息； chipOptionsList 保存chip勾选信息；  shelfLifeList 保存保质期填写信息
    val insertItemList = MutableLiveData<MutableList<Item>>()
    val chipOptionsList = MutableLiveData<MutableList<ChipOptions>>()
    val shelfLifeList = MutableLiveData<MutableList<String>>()
    val finishOrNotList = MutableLiveData<MutableList<Boolean>>()
    val showInfoButtonList = MutableLiveData<MutableList<Boolean>>()
    //识图API获取的标签
    val photoResearchList = MutableLiveData<MutableList<MutableList<ResultInfo>>>()


    var token:String = ""

    fun initViewModelData(arrayFile : Array<File>){
        val listShowInfo = mutableListOf<Boolean>()
        val listItem = mutableListOf<Item>()
        val listChip = mutableListOf<ChipOptions>()
        val listShelfLife = mutableListOf<String>()
        val listPhotoResearch = mutableListOf<MutableList<ResultInfo>>()
        val listFinish = mutableListOf<Boolean>()
        for (position in arrayFile.indices){
            listItem.add(createItem())
            listChip.add(ChipOptions())
            listShelfLife.add("")
            listPhotoResearch.add(mutableListOf(ResultInfo(0F,"","")))
            listFinish.add(false)
            listShowInfo.add(false)
        }
        insertItemList.value = listItem
        chipOptionsList.value = listChip
        shelfLifeList.value = listShelfLife
        photoResearchList.value = listPhotoResearch
        finishOrNotList.value = listFinish
        showInfoButtonList.value = listShowInfo
    }

    //删除图片队列。
    val deleteList = mutableListOf<File>()





    //识图API查询类  photoListList为点击刷新按钮的处理队列，全部拿到了再返回，这里不用观察
    val photoListList = mutableListOf<MutableList<List<ResultInfo>>>()
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




    //Repository工具库
    fun getLeisureRoom(type: Int,large : Int,medium : Int,listener: Repository.OnLoadRoomListener) = Repository.getLeisureRoom(type,large,medium,listener)
    fun loadLargeBoxId(onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadLargeBoxId(onLoadItemListListener)
    fun loadMediumBoxId(largeBoxId : Int ,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadMediumBoxId(largeBoxId,onLoadItemListListener)
    fun loadSmallBoxId(largeBoxId : Int ,mediumBoxId : Int,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadSmallBoxId(largeBoxId,mediumBoxId,onLoadItemListListener)
    fun loadItemById(itemId : Long , loadItemListener: Repository.OnLoadItemListener) = Repository.loadItemById(itemId,loadItemListener)
    fun insertItem(item : Item,loadItemListListener: Repository.OnLoadItemIdListener?) = Repository.insertItem(item,loadItemListListener)
    fun deleteItem(item : Item) = Repository.deleteItem(item)
    fun updateItem(item : Item) = Repository.updateItem(item)
    fun clearItem() = Repository.clearAllItem()
}
