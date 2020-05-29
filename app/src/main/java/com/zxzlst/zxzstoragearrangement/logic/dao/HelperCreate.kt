package com.zxzlst.zxzstoragearrangement.logic.dao

import android.content.res.Resources
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication

fun createItem(largeBoxId : Int = 0,
               mediumBoxId : Int = 0,
               smallBoxId : Int = 0,
               itemName : String = ZxzStorageApplication.context.resources.getString(R.string.default_itemName),
               itemType : String = ZxzStorageApplication.context.resources.getString(R.string.default_itemType),
               editDate : String = ZxzStorageApplication.context.resources.getString(R.string.default_date),
               startDate : String = ZxzStorageApplication.context.resources.getString(R.string.default_date),
               endDate : String = ZxzStorageApplication.context.resources.getString(R.string.default_date),
               itemNumber : Int = 0,
               categoryId : Int = 0,
               customTag : String = "",
               mainPhotoPath : String = "",
               itemPrice : Float = 0F,
               consumeType : Int = 0,
               privacy : Int = 0,
               noticeDate : String = ZxzStorageApplication.context.resources.getString(R.string.default_date),
               noticeContent : String = "",
               customDescribe : String = "",
               brand : String = "") : Item{
    return Item(largeBoxId,mediumBoxId,smallBoxId,itemName,itemType,editDate,startDate,endDate,itemNumber,categoryId,customTag,
    mainPhotoPath,itemPrice,consumeType,privacy,noticeDate,noticeContent,customDescribe,brand)
}