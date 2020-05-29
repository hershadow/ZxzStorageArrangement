package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.dao.Item

class ThirdViewModel : ViewModel() {

    var allItemLive : LiveData<List<Item>> = Repository.allItemLive

    // TODO: Implement the ViewModel
}
