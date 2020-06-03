package com.zxzlst.zxzstoragearrangement.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.editmodule.EditActivityForMain
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.transFromFieldText
import kotlinx.android.synthetic.main.first_fragment.*
import kotlinx.android.synthetic.main.firstfragment_item.view.*
import java.lang.Exception
import java.lang.reflect.Field

class FirstAdapter(private val fragment: Fragment,private val researchList : List<Item>) : RecyclerView.Adapter<FirstAdapter.ViewHolder>(){

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.firstfragment_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = researchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //图片放这里太卡顿，setbitmap每次都会创建新的，导致内存占用高
        val photoPath = Repository.mainPhotoPathToMipMap(researchList[position].mainPhotoPath)
        Glide.with(holder.itemView).load(photoPath).placeholder(R.drawable.ic_image_black_24dp).into(holder.itemView.firstItem_imageView)
        holder.itemView.firstItem_TextView_L1.text = researchList[position].itemName
        holder.itemView.firstItem_TextView_R1.text = researchList[position].itemType
        holder.itemView.firstItem_TextView_L2.text = researchList[position].editDate
        holder.itemView.firstItem_TextView_R2.text = researchList[position].brand
        //获得查找结果相关的词条来进行显示。
        try {
            var tag = 0
            loop@ for (info in researchList[position].javaClass.declaredFields){
                if (info.name == "brand" || info.name == "itemName" || info.name == "itemType" || info.name == "itemNumber" || info.name == "itemPrice" || info.name == "noticeContent" || info.name == "noticeDate" || info.name == "customDescribe"){
                    val access = info.isAccessible
                    info.isAccessible = true
                    if (info.get(researchList[position]).toString().contains(fragment.firstFragment_searchEditText.text.toString())){
                        when(tag){
                            0 -> {
                                tag = 1
                                val spanText = SpannableString("${transFromFieldText(info.name)} : " + info.get(researchList[position]).toString())
                                spanText.setSpan(
                                    ForegroundColorSpan(Color.parseColor("#FF0000")),info.get(researchList[position]).toString()
                                        .indexOf(fragment.firstFragment_searchEditText.text.toString()) + "${transFromFieldText(info.name)} : ".length,info.get(researchList[position]).toString()
                                        .indexOf(fragment.firstFragment_searchEditText.text.toString()) + "${transFromFieldText(info.name)} : ".length + fragment.firstFragment_searchEditText.text.toString().length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                holder.itemView.firstItem_TextView_L3.text = spanText
                                holder.itemView.firstItem_TextView_L3.visibility = View.VISIBLE
                            }
                            1 -> {
                                tag = 2
                                val spanText = SpannableString("${transFromFieldText(info.name)} : " + info.get(researchList[position]).toString())
                                spanText.setSpan(
                                    ForegroundColorSpan(Color.parseColor("#FF0000")),info.get(researchList[position]).toString()
                                        .indexOf(fragment.firstFragment_searchEditText.text.toString()) + "${transFromFieldText(info.name)} : ".length,info.get(researchList[position]).toString()
                                        .indexOf(fragment.firstFragment_searchEditText.text.toString()) + "${transFromFieldText(info.name)} : ".length + fragment.firstFragment_searchEditText.text.toString().length,
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                holder.itemView.firstItem_TextView_L4.text = spanText
                                holder.itemView.firstItem_TextView_L4.visibility = View.VISIBLE
                            }
                            else -> break@loop
                        }
                    }
                    Log.d("zxzzxzzxz","${info.name} : ${info.get(researchList[position])}")
                    info.isAccessible = access
                }
            }
        }catch (e:Exception){
            Log.d("zxzzxzzxz",e.toString())
        }




        //点击item的时候，启动activity进入详情页面
        holder.itemView.setOnClickListener {
            EditActivityForMain.activityStart(fragment.requireContext(),researchList[position].id,0)
        }
    }

}




