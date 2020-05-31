package com.zxzlst.zxzstoragearrangement.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.editmodule.EditActivityForMain
import com.zxzlst.zxzstoragearrangement.logic.dao.Item

class FirstAdapter(private val fragment: Fragment,private val researchList : List<Item>) : RecyclerView.Adapter<FirstAdapter.ViewHolder>(){

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val secondItemImage: ImageView = view.findViewById(R.id.secondItem_imageView)
        val secondItemText: TextView = view.findViewById(R.id.secondItem_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.secondfragment_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = researchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //图片放这里太卡顿，setbitmap每次都会创建新的，导致内存占用高
        val photoPath = Repository.mainPhotoPathToMipMap(researchList[position].mainPhotoPath)
        holder.secondItemImage.setImageBitmap(BitmapFactory.decodeFile(photoPath))
        //holder.secondItemImage.setImageDrawable(setUpImage(itemList[position].mainPhotoPath))


        holder.secondItemText.text = researchList[position].itemName
        //点击item的时候，启动activity进入详情页面
        holder.itemView.setOnClickListener {
            EditActivityForMain.activityStart(fragment.requireContext(),researchList[position].id,0)
        }
    }

}




