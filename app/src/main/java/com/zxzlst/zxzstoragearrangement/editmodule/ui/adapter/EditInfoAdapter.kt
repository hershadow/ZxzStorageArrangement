package com.zxzlst.zxzstoragearrangement.editmodule.ui.adapter

import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication
import com.zxzlst.zxzstoragearrangement.logic.dao.Item

class EditInfoAdapter(val context: Context,val recentItem : Item) : RecyclerView.Adapter<EditInfoAdapter.ViewHolder>(){
    inner class ViewHolder(view : View) :RecyclerView.ViewHolder(view){
        val seeItemTextViewLeft : TextView = view.findViewById(R.id.editSee_recyclerInfo_textViewLeft)
        val seeItemTextViewRight : TextView = view.findViewById(R.id.editSee_recyclerInfo_textViewRight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.edit_see_recycler_info,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = formatItemInfo.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.seeItemTextViewLeft.text = formatItemInfo[position].describe
        holder.seeItemTextViewRight.text = formatItemInfo[position].info
    }

    private val formatItemInfo : List<DoubleString> = convertItemToDoubleStringList(recentItem)


    class DoubleString(val describe:String , val info : String)
    private fun convertItemToDoubleStringList(item : Item) : List<DoubleString>{
        val list : MutableList<DoubleString> = mutableListOf()
        if (item.editDate != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_editDate),item.editDate))
        if (item.startDate != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_startDate),item.startDate))
        if (item.endDate != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_endDate),item.endDate))
        if (item.itemNumber != 1) list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_number),item.itemNumber.toString()))
        if (item.customTag != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_tag),item.customTag))
        if (item.consumeType != 0) list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_consumeType),item.consumeType.toString()))
        if (item.noticeDate != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_noticeDate),item.noticeDate))
        if (item.noticeContent != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_noticeContent),item.noticeContent))
        if (item.customDescribe != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_customDescribe),item.customDescribe))
        if (item.brand != "") list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_brand),item.brand))
        if (item.itemPrice != 0F) list.add(DoubleString(ZxzStorageApplication.context.resources.getString(R.string.editSee_itemPrice),item.itemPrice.toString()))
        return list
    }
}