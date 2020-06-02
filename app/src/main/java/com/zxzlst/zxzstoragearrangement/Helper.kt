package com.zxzlst.zxzstoragearrangement

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment.CameraInsertViewModel
import java.lang.Exception
import java.util.*

/*
private val repositoryImagePathNormal : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/normal")
private val repositoryImagePathMipMap : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/mipmap")
private val repositoryImagePathTemporary : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/temporary")
private val repositoryImagePathCrash : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/crash")
*/
const val REQUEST_CODE_PERMISSIONS = 10

//设置选项从这里进
/***
 * 目前的设置选项：      takingPhotoRotate   默认""不旋转 ， 其他旋转
 *                      typeInfoContainKeyword    默认不包含，其他包含
 */
fun zxzOptionSharedPreferences(string : String) : String?{
    return ZxzStorageApplication.context.getSharedPreferences("option",Context.MODE_PRIVATE).getString(string,"")
}

//chip 类的设定  ，用来给viewModel创建List保存用户选择 用
class ChipOptions(var chipOptionBrand : Boolean = false,
                  var chipOptionNumber : Boolean = false,
                  var chipOptionPrice: Boolean = false,
                  var chipOptionCustomDescribe: Boolean = false,
                  var chipOptionStartDate: Boolean = false,
                  var chipOptionEndDate: Boolean = false,
                  var chipOptionShelfLife: Boolean = false,
                  var chipOptionNoticeDate: Boolean = false,
                  var chipOptionNoticeContent: Boolean = false)


//生产日期 截至日期 与 保质期 的判断，传入3个参数，其中传入的3个参数中有一个为空 ,返回的为传空的那一个
fun caculateStartEndShelfLifeDate(startDate : String?,endDate : String?,shelfLife : Int?) : Any{
    val calendar = Calendar.getInstance()
    val subclendar = Calendar.getInstance()
    try {
        when{
            startDate == null && endDate != null && shelfLife != null -> {
                calendar.time = Repository.insertDateFormat.parse(endDate)!!
                calendar.add(Calendar.MONTH,0 - shelfLife)
                return Repository.insertDateFormat.format(calendar.time) as String
            }
            startDate != null && endDate == null && shelfLife != null -> {
                calendar.time = Repository.insertDateFormat.parse(startDate)!!
                calendar.add(Calendar.MONTH, shelfLife)
                return Repository.insertDateFormat.format(calendar.time) as String
            }
            startDate != null && endDate != null && shelfLife == null -> {
                calendar.time = Repository.insertDateFormat.parse(startDate)!!
                subclendar.time = Repository.insertDateFormat.parse(endDate)!!
                return (subclendar.timeInMillis - calendar.timeInMillis / (1000 * 60 * 60 *24))/ 30
            }
            else -> {
                Log.d("zxzzxzzxz","出错，生产日期截止日期保质期有问题")
            }
        }
    }catch (e : Exception){
        Log.d("zxzZxz",e.toString())
    }
    return 0
}

//将Item的类名转换为界面上显示的中文字           不含标签的，标签的搜索栏是另一个，本搜索栏不搜标签信息
fun transFromFieldText(itemFields : String): String{
    return when(itemFields){
        "brand" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_brand)
        "itemPrice" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_itemPrice)
        "itemNumber" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_number)
        "itemName" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_itemName)
        "itemType" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_itemType)
        "editDate" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_editDate)
        "startDate" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_startDate)
        "noticeContent" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_noticeContent)
        "noticeDate" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_noticeDate)
        "customDescribe" -> ZxzStorageApplication.context.resources.getString(R.string.editSee_customDescribe)
        else -> ""
    }
}


