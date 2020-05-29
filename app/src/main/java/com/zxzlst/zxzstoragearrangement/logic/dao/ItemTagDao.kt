package com.zxzlst.zxzstoragearrangement.logic.dao

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemTagDao {
    @Insert
    fun insertItemTag(itemTag: ItemTag):Long

    @Update
    fun updateItem(newItemTag : ItemTag)

    @Delete
    fun deleteItem(ItemTag : ItemTag)

    @Query("select * from ItemTag")
    fun loadAllItem():LiveData<List<ItemTag>>
}