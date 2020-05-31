package com.zxzlst.zxzstoragearrangement.logic.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.regex.Pattern

@Dao
interface ItemDao {
    @Insert
    fun insertItem(item: Item):Long

    @Update
    fun updateItem(newItem : Item)

    @Delete
    fun deleteItem(Item : Item)

    @Query("select * from Item")
    fun loadAllItem():LiveData<List<Item>>

    @Query("select * from Item Where Id = :itemId")
    fun loadItemById(itemId : Long): Item

    @Query("delete from Item")
    fun clearAllItem()

    //获取Large ROOM空间
    @Query("select * from Item Where categoryId = 1")
    fun loadAllLargeBoxType() : List<Item>

    //获取medium ROOM空间
    @Query("select * from Item Where (categoryId = 2 And largeBoxId = :largeId)")
    fun loadAllMediumBoxType(largeId : Int) : List<Item>

    //获取small ROOM空间
    @Query("select * from Item Where (categoryId = 3 And largeBoxId = :largeId And mediumBoxId = :mediumId)")
    fun loadAllSmallBoxType(largeId : Int,mediumId : Int) : List<Item>

    //模糊查询
    @Query("SELECT * FROM Item WHERE (itemName || itemType || itemNumber || itemPrice || noticeContent || customDescribe || brand) LIKE :pattern ORDER BY Id DESC")
    fun searchForResult(pattern: String) : LiveData<List<Item>>
}