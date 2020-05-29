package com.zxzlst.zxzstoragearrangement.insertmodule.ui.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment.BottomDialogSheetFragment
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment.CameraInsertViewModel
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import kotlinx.android.synthetic.main.camera_bottomsheetdialog.view.*
import kotlinx.android.synthetic.main.camera_insert_pager_info.view.*

//对item做最后处理 ， 包括1.对图片进行转移 2.图片地址的注入 3.BottomListDialog来选择盒子 4.盒子id的注入 5.判断日期是否合格

//设置保存按钮的事件
fun dialogSolver(holder: PagerPhotoViewHolder,fragmentForUse : Fragment,adapter:PagerPhotoListAdapter){
    holder.itemView.camera_insert_pager_saveButton.setOnClickListener {
        val bottomDialogFragment =  BottomDialogSheetFragment()
        bottomDialogFragment.fragmentForUse = fragmentForUse
        bottomDialogFragment.holder = holder
        bottomDialogFragment.adapter = adapter
        bottomDialogFragment.show(fragmentForUse.parentFragmentManager,"dialog")


        //createBottomDialog(holder,finalItem,fragmentForUse)
        /*
        Repository.insertItem(finalItem)
        Toast.makeText(fragmentForUse.context,"添加成功",Toast.LENGTH_SHORT).show()
        holder.itemView.camera_insert_scrollView.visibility = View.GONE
        holder.itemView.camera_insert_scrollView2.visibility = View.GONE
         */
    }
}

//创建dialog
private fun createBottomDialog(holder : PagerPhotoViewHolder,item : Item,fragmentForUse: Fragment) : Item {
    val returnItem : Item = item
    var view : View = LayoutInflater.from(fragmentForUse.requireContext()).inflate(R.layout.camera_bottomsheetdialog,null,false)
    val bottomSheetDialog = BottomSheetDialog(fragmentForUse.requireContext())
    view = dialogAddClickEvent(view,holder,fragmentForUse)
    bottomSheetDialog.setContentView(view)
    bottomSheetDialog.window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.bottomdialog_color)
    bottomSheetDialog.window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.bottomdialog_cornner)
    if (bottomSheetDialog.isShowing) bottomSheetDialog.dismiss()
    else bottomSheetDialog.show()

    return returnItem
}

//设置dialog中填充的view的控件交互规则
private fun dialogAddClickEvent(view: View, holder: PagerPhotoViewHolder,fragmentForUse : Fragment) : View {

    view.bottomDialog_add_firstChip.setOnClickListener {
        val editText = EditText(fragmentForUse.requireContext())
        val builder : AlertDialog.Builder = AlertDialog.Builder(fragmentForUse.requireContext())
            .setTitle("输入空间名称").setView(editText).setPositiveButton("确认创建"
            ) { dialog, which ->
                val newChip = fragmentForUse.layoutInflater.inflate(R.layout.single_chip_layout,view.bottomDialog_firstChipGroup,false) as Chip
                newChip.text = editText.text.toString()
                //给chip加一个tag来拿到该空间的ID
                newChip.setTag(1, Repository.createLargeRoom(editText.text.toString()))
                view.bottomDialog_firstChipGroup.addView(newChip)
            }.setNegativeButton("取消"
            ) { dialog, which ->
                dialog.dismiss()
            }
        builder.create().show()
    }




    return view
}