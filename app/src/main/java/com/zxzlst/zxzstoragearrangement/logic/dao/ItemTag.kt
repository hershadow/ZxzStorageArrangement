package com.zxzlst.zxzstoragearrangement.logic.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemTag(val description : String) {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}