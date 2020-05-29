package com.zxzlst.zxzstoragearrangement.logic.dao

import android.service.autofill.CustomDescription
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Item(var largeBoxId : Int,       //最大类的ID
                var mediumBoxId : Int,      //中间类的ID
                var smallBoxId : Int,       //最小类的ID
                var itemName : String,      //名称      如果category不是0，那这就是BOX的名字
                var itemType : String,      //类别
                var editDate : String,        //编辑日期
                var startDate : String,       //生产日期
                var endDate : String,         //过期时间
                var itemNumber : Int,       //个数
                var categoryId : Int,       //种类ID，物品为0，large为1，medium为2，small为3（用于区分是否为目录）
                var customTag : String,     //自定义标签，用字母隔开，包含多个TAG的ID，由另一个表指定
                var mainPhotoPath : String, //主图路径
                var itemPrice : Float,      //价格
                var consumeType : Int,      //是否为消耗品，0不是，数字大小表示短期长期
                var privacy : Int,          //私密性
                var noticeDate : String,      //提醒日期
                var noticeContent : String, //提醒内容
                var customDescribe : String,//用户自己输入的描述
                var brand : String          // 品牌
                ) {

    @PrimaryKey(autoGenerate = true)
    var id : Long = 0

}