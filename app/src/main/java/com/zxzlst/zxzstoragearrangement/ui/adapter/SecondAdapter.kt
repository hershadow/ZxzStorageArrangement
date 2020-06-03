package com.zxzlst.zxzstoragearrangement.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.editmodule.EditActivityForMain
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.model.mainViewModel
import kotlinx.android.synthetic.main.secondfragment_item.view.*
import org.w3c.dom.Text
import java.io.File
import java.util.*

class SecondAdapter(val context: Context) : RecyclerView.Adapter<SecondAdapter.ViewHolder>() {
    private var itemList : List<Item> = listOf()
    private var showList : List<Item> = listOf()
    private var currentCategory = 1
    private var currentRoomId = mutableListOf(0,0,0)
    fun setItemList(list : List<Item>){
        this.itemList = list
        changePage()
    }

    inner class ViewHolder(view : View) :RecyclerView.ViewHolder(view){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.secondfragment_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = showList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.secondItem_textView.text = showList[position].itemName
        if(showList[position].categoryId == 0){
        //图片放这里太卡顿，setbitmap每次都会创建新的，导致内存占用高
        val photoPath = Repository.mainPhotoPathToMipMap(showList[position].mainPhotoPath)

        Glide.with(holder.itemView).load(photoPath).placeholder(R.drawable.ic_image_black_24dp).into(holder.itemView.secondItem_imageView)
        //holder.secondItemImage.setImageBitmap(BitmapFactory.decodeFile(photoPath))

        //点击item的时候，启动activity进入详情页面 如果是空间，就进入空间

            holder.itemView.setOnClickListener {
                EditActivityForMain.activityStart(this.context,itemList[position].id,0)
            }
        }else{
            Glide.with(holder.itemView).load(R.drawable.ic_baseline_all_inbox_24).placeholder(R.drawable.ic_image_black_24dp).into(holder.itemView.secondItem_imageView)
            holder.itemView.setOnClickListener {
                currentRoomId[currentCategory - 1] = when(currentCategory){
                    1 -> showList[position].largeBoxId
                    2 -> showList[position].mediumBoxId
                    3 -> showList[position].smallBoxId
                    //这个else按理说会出错
                    else -> 0
                }
                currentCategory += 1
                changePage()
                notifyDataSetChanged()
            }
        }

    }

    //该方法用于显示当前层的物品与空间
    private fun changePage(){
        val addList = mutableListOf<Item>()
        addList.clear()
        for (item in itemList){
            if ((item.categoryId == 0 && item.largeBoxId == currentRoomId[0] && item.mediumBoxId == currentRoomId[1] && item.smallBoxId == currentRoomId[2]) ||
                (currentCategory == 1 && item.categoryId == 1) ||
                (currentCategory == 2 && item.categoryId == 2 && item.largeBoxId == currentRoomId[0])||
                (currentCategory == 3 && item.categoryId == 3 && item.largeBoxId == currentRoomId[0] && item.mediumBoxId == currentRoomId[1])){
                addList.add(item)
            }
        }
        showList = sortShowList(addList)
    }

    //该方法用于整理list来实现ROOM显示靠前。
    private fun sortShowList(list: List<Item>) : List<Item>{
        val sortList = mutableListOf<Item>()
        for (item in list){
            if (item.categoryId != 0) sortList.add(item)
        }
        for (item in list){
            if (item.categoryId == 0) sortList.add(item)
        }
        return sortList
    }

    //返回功能
    fun backRoom(){
        when(currentCategory){
            2 -> {
                currentCategory = 1
                currentRoomId = mutableListOf(0,0,0)
            }
            3 -> {
                currentCategory = 2
                currentRoomId = mutableListOf(currentRoomId[0],0,0)
            }
            4 -> {
                currentCategory = 2
                currentRoomId = mutableListOf(currentRoomId[0],currentRoomId[1],0)
            }
            else ->{}
        }
    }

}