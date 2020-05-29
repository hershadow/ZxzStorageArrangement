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
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.editmodule.EditActivityForMain
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.model.mainViewModel
import kotlinx.android.synthetic.main.secondfragment_item.view.*
import org.w3c.dom.Text
import java.io.File

class SecondAdapter(val context: Context) : RecyclerView.Adapter<SecondAdapter.ViewHolder>() {
    private var itemList : List<Item> = listOf()
    fun setItemList(list : List<Item>){
        this.itemList = list
    }

    inner class ViewHolder(view : View) :RecyclerView.ViewHolder(view){
        val secondItemImage:ImageView = view.findViewById(R.id.secondItem_imageView)
        val secondItemText: TextView = view.findViewById(R.id.secondItem_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.secondfragment_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //图片放这里太卡顿，setbitmap每次都会创建新的，导致内存占用高
        val photoPath = Repository.mainPhotoPathToMipMap(itemList[position].mainPhotoPath)
        holder.secondItemImage.setImageBitmap(BitmapFactory.decodeFile(photoPath))
        //holder.secondItemImage.setImageDrawable(setUpImage(itemList[position].mainPhotoPath))


        holder.secondItemText.text = itemList[position].itemName
        //点击item的时候，启动activity进入详情页面
        holder.itemView.setOnClickListener {
            EditActivityForMain.activityStart(this.context,itemList[position].id,0)
        }
    }


}