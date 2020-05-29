package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication
import com.zxzlst.zxzstoragearrangement.logic.dao.AppDatabase
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import kotlin.concurrent.thread

class SecondViewModel : ViewModel() {
    var allItemLive : LiveData<List<Item>> = Repository.allItemLive

    fun insertItem(item : Item) = Repository.insertItem(item,null)
    fun deleteItem(item : Item) = Repository.deleteItem(item)
    fun updateItem(item : Item) = Repository.updateItem(item)
    fun clearItem() = Repository.clearAllItem()
    fun loadLargeBoxId(onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadLargeBoxId(onLoadItemListListener)
    fun loadMediumBoxId(largeBoxId : Int ,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadMediumBoxId(largeBoxId,onLoadItemListListener)
    fun loadSmallBoxId(largeBoxId : Int ,mediumBoxId : Int,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadSmallBoxId(largeBoxId,mediumBoxId,onLoadItemListListener)
    fun loadItemById(itemId : Long , loadItemListener: Repository.OnLoadItemListener) = Repository.loadItemById(itemId,loadItemListener)





}
