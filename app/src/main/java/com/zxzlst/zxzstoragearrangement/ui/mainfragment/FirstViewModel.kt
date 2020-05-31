package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.dao.Item

class FirstViewModel : ViewModel() {
    //模糊查询相关
    private val searchLiveData = MutableLiveData<String>()

    val resultList = ArrayList<Item>()

    val resultLiveData = Transformations.switchMap(searchLiveData){
        Repository.searchForResult(it)
    }

    fun searchForResult(query: String){
        searchLiveData.value = query
    }



    fun insertItem(item : Item) = Repository.insertItem(item,null)
    fun deleteItem(item : Item) = Repository.deleteItem(item)
    fun updateItem(item : Item) = Repository.updateItem(item)
    fun clearItem() = Repository.clearAllItem()
    fun loadLargeBoxId(onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadLargeBoxId(onLoadItemListListener)
    fun loadMediumBoxId(largeBoxId : Int ,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadMediumBoxId(largeBoxId,onLoadItemListListener)
    fun loadSmallBoxId(largeBoxId : Int ,mediumBoxId : Int,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadSmallBoxId(largeBoxId,mediumBoxId,onLoadItemListListener)
    fun loadItemById(itemId : Long , loadItemListener: Repository.OnLoadItemListener) = Repository.loadItemById(itemId,loadItemListener)

}
