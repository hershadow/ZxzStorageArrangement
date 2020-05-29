package com.zxzlst.zxzstoragearrangement.editmodule.ui.fragment

import androidx.lifecycle.ViewModel
import com.zxzlst.zxzstoragearrangement.Repository

class EditSeeViewModel : ViewModel() {

    fun loadLargeBoxId(onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadLargeBoxId(onLoadItemListListener)
    fun loadMediumBoxId(largeBoxId : Int ,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadMediumBoxId(largeBoxId,onLoadItemListListener)
    fun loadSmallBoxId(largeBoxId : Int ,mediumBoxId : Int,onLoadItemListListener: Repository.OnLoadItemListListener) = Repository.loadSmallBoxId(largeBoxId,mediumBoxId,onLoadItemListListener)
    fun loadItemById(itemId : Long , loadItemListener: Repository.OnLoadItemListener) = Repository.loadItemById(itemId,loadItemListener)

    // TODO: Implement the ViewModel
}
