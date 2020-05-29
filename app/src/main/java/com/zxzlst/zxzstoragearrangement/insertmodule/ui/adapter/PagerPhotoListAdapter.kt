package com.zxzlst.zxzstoragearrangement.insertmodule.ui.adapter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment.CameraInsertViewModel
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import com.zxzlst.zxzstoragearrangement.logic.model.ResultInfo
import com.zxzlst.zxzstoragearrangement.zxzOptionSharedPreferences
import kotlinx.android.synthetic.main.camera_bottomsheetdialog.*
import kotlinx.android.synthetic.main.camera_bottomsheetdialog.view.*
import kotlinx.android.synthetic.main.camera_insert_pager_info.view.*
import kotlinx.android.synthetic.main.camera_insert_pager_info.view.camera_card_startDate_image
import kotlinx.android.synthetic.main.camera_insert_pager_info.view.camera_card_startDate_showText
import kotlinx.android.synthetic.main.camera_insert_pager_info.view.camera_insert_pager_saveButton
import java.io.File
import java.lang.Exception
import java.util.*

class PagerPhotoListAdapter(fragment: Fragment) : ListAdapter<File,PagerPhotoViewHolder>(DiffCallback) {
    private  val viewModel: CameraInsertViewModel by lazy { ViewModelProviders.of(fragment).get(CameraInsertViewModel::class.java) }
    private val fragmentForUse = fragment
    private var currentPosition: Int = 0
    //这个holder暂定给刷新InfoList用的
    private var currentHolder : PagerPhotoViewHolder? = null
    private lateinit var photoInfoList : MutableList<MutableList<ResultInfo>>

    private val temporaryFiles by lazy { Repository.repositoryImagePathTemporary.listFiles()!!.apply {
        Arrays.sort(this) { o1, o2 ->
            val diff : Long = (o1.lastModified()) - (o2.lastModified())
            when{
                diff> 0 -> 1
                diff == 0L -> 0
                diff < 0 -> -1
                else -> 0
            }
        }
    } }
/*
    private val temporaryFiles : Array<File>? = Repository.repositoryImagePathTemporary.listFiles()

    val temporaryFiles = Repository.repositoryImagePathTemporary.listFiles()!!
    Arrays.sort(temporaryFiles) { o1, o2 ->
        val diff : Long = (o1.lastModified()) - (o2.lastModified())
        when{
            diff> 0 -> 1
            diff == 0L -> 0
            diff < 0 -> -1
            else -> 0
        }
    }

 */

    object DiffCallback : DiffUtil.ItemCallback<File>(){
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem == newItem
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.camera_insert_pager_info,parent,false).apply {
            return  PagerPhotoViewHolder(this)
        }

    }

    override fun onViewAttachedToWindow(holder: PagerPhotoViewHolder) {
        Log.d("zxzzxzzxz",holder.adapterPosition.toString() + "attach")

        //TODO 这俩得分清楚，current需要通过和detach一起进行判断获得

        //将当前屏幕展示的VIEW的position保存下来供其他函数使用  holder给refresh用
        currentPosition = holder.adapterPosition
        currentHolder = holder


        //刷新获取photoInfoList
        if (viewModel.photoResearchList.value != null) photoInfoList = viewModel.photoResearchList.value!!

        //根据photoInfoList调整按钮的显示
        if (photoInfoList[currentPosition][0].score > 0.1){
            addButtonEventForInfo(holder,photoInfoList[currentPosition])
        }
        if (viewModel.showInfoButtonList.value!= null){
            if (viewModel.showInfoButtonList.value!![currentPosition]) {
                holder.itemView.camera_cardView_nameInfo.visibility = View.VISIBLE
                holder.itemView.camera_cardView_TypeInfo.visibility = View.VISIBLE
            }else{
                holder.itemView.camera_cardView_nameInfo.visibility = View.GONE
                holder.itemView.camera_cardView_TypeInfo.visibility = View.GONE
            }
        }

        //编辑框的编辑事件  文本改变时传给viewModel
        initEditTextListener(holder.itemView,holder.adapterPosition)


        if (viewModel.finishOrNotList.value!= null){
            if (viewModel.finishOrNotList.value!![currentPosition]){
                holder.itemView.camera_insert_scrollView2.visibility = View.GONE
                holder.itemView.camera_insert_scrollView.visibility = View.GONE
                holder.itemView.camera_insert_pager_saveButton.isClickable = false
            }else{
                holder.itemView.camera_insert_scrollView2.visibility = View.VISIBLE
                holder.itemView.camera_insert_scrollView.visibility = View.VISIBLE
                holder.itemView.camera_insert_pager_saveButton.isClickable = true
            }
        }
        //保存按钮的点击事件
        if (viewModel.insertItemList.value?.get(holder.adapterPosition) != null){
            dialogSolver(holder,fragmentForUse,this)
            holder.itemView.camera_insert_pagerr_deleteButton.setOnClickListener {
                try {
                    //TODO 在这里来个dialog ，选择确定的话再删除
                    viewModel.finishOrNotList.value!![currentPosition] = true
                    refreshInfoList(2)
                    //不要在这里删除，会打乱队列，在退出时删除,这里打上标记
                    viewModel.deleteList.add(temporaryFiles[currentPosition])
                    Toast.makeText(fragmentForUse.requireContext(), "已添加标记，将于返回拍照界面时进行删除",Toast.LENGTH_LONG).show()
                }catch (e:Exception){
                    Toast.makeText(fragmentForUse.requireContext(), "删除失败:$e",Toast.LENGTH_LONG).show()
                }

            }
        }
        else Toast.makeText(fragmentForUse.context,"异常：未找到对应物品信息",Toast.LENGTH_SHORT).show()


        super.onViewAttachedToWindow(holder)
    }



    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        //获取viewPager的当前一栏 的对应的viewModel中的存储Item
        /*if (viewModel.getViewModelItem(position) == null) return
        val itemInViewModel : Item = viewModel.getViewModelItem(position)!!
         */
        holder.itemView.camera_insert_pager_imageView.setImageBitmap(BitmapFactory.decodeFile(Repository.repositoryImagePathTemporaryMipmap.path + "/" + temporaryFiles[position].name))

        //chip的check事件，check的时候传给viewModel
        initChipCheckListener(holder.itemView,holder.adapterPosition)
        //日期选择器
        initDateChooseOptions(holder.itemView)

        /*
        打印viewModel的内容
        for (unit in viewModel.insertItemList.value!!){
            Log.d("zxzzxzzxz", unit.itemName + viewModel.insertItemList.value!!.indexOf(unit))
        }
         */

        //observer       chip   选择的TAG
        viewModel.chipOptionsList.observe(fragmentForUse, Observer {
            it[position].apply {
                holder.itemView.chip_brand.isChecked = chipOptionBrand
                holder.itemView.chip_number.isChecked = chipOptionNumber
                holder.itemView.chip_price.isChecked = chipOptionPrice
                holder.itemView.chip_customDescribe.isChecked = chipOptionCustomDescribe
                holder.itemView.chip_startDate.isChecked = chipOptionStartDate
                holder.itemView.chip_endDate.isChecked = chipOptionEndDate
                holder.itemView.chip_shelfLife.isChecked = chipOptionShelfLife
                holder.itemView.chip_noticeDate.isChecked = chipOptionNoticeDate
                holder.itemView.chip_noticeContent.isChecked = chipOptionNoticeContent
            }
        })

        //observer      item  填写的内容
        viewModel.insertItemList.observe(fragmentForUse, Observer {
            it[position].apply {
                holder.itemView.apply {
                    Log.d("zxzzxzzxz",position.toString() + "item observe get :" + itemName)
                    camera_card_itemName_editText.setText(itemName)
                    camera_card_itemType_editText.setText(itemType)
                    camera_card_number_editText.setText(itemNumber.toString())
                    camera_card_price_editText.setText(itemPrice.toString())
                    camera_card_brand_editText.setText(brand)
                    camera_card_customDescribe_editText.setText(customDescribe)
                    camera_card_startDate_editText.setText(startDate)
                    camera_card_endDate_editText.setText(endDate)
                    camera_card_noticeDate_editText.setText(noticeDate)
                    camera_card_noticeContent_editText.setText(noticeContent)
                }
            }
        })


        //observer shelfLife
        viewModel.shelfLifeList.observe(fragmentForUse, Observer {
            holder.itemView.camera_card_shelfLife_editText.setText(it[position])
        })




    }

    //photoInfo  Int type=1:立即刷新当前页面的photoInfo内容,并显示按钮 用于在手动刷新获取完数据后调用  type=2也刷新完成后的可视化更改 type=3
    fun refreshInfoList(type : Int){
        when(type){
            1 -> {
                if (viewModel.photoResearchList.value != null) photoInfoList = viewModel.photoResearchList.value!!
                if (currentHolder != null){
                    addButtonEventForInfo(currentHolder!!,photoInfoList[currentPosition])
                    if (viewModel.showInfoButtonList.value!= null){
                        if (viewModel.showInfoButtonList.value!![currentPosition]) {
                            currentHolder!!.itemView.camera_cardView_nameInfo.visibility = View.VISIBLE
                            currentHolder!!.itemView.camera_cardView_TypeInfo.visibility = View.VISIBLE
                        }else{
                            currentHolder!!.itemView.camera_cardView_nameInfo.visibility = View.GONE
                            currentHolder!!.itemView.camera_cardView_TypeInfo.visibility = View.GONE
                        }
                    }
                }
            }
            2 ->{
                if (viewModel.finishOrNotList.value!= null){
                    if (viewModel.finishOrNotList.value!![currentPosition] && currentHolder != null){
                        currentHolder!!.itemView.camera_insert_scrollView2.visibility = View.GONE
                        currentHolder!!.itemView.camera_insert_scrollView.visibility = View.GONE
                        currentHolder!!.itemView.camera_insert_pager_saveButton.isClickable = false
                        Toast.makeText(fragmentForUse.requireContext(),"删除/添加 功能完成，对象已从相册移除",Toast.LENGTH_SHORT).show()
                    }else{
                        currentHolder!!.itemView.camera_insert_scrollView2.visibility = View.VISIBLE
                        currentHolder!!.itemView.camera_insert_scrollView.visibility = View.VISIBLE
                        currentHolder!!.itemView.camera_insert_pager_saveButton.isClickable = true
                    }
                }
            }
            else -> return
        }


    }

    //为InfoList添加按钮事件
    private fun addButtonEventForInfo(holder : PagerPhotoViewHolder, info : MutableList<ResultInfo>){
        if (info[0].score > 0.1) {
            if (viewModel.showInfoButtonList.value!= null) viewModel.showInfoButtonList.value!![holder.adapterPosition] = true
            holder.itemView.camera_cardView_nameInfo.setOnClickListener {
                val keywordItems = mutableListOf<String>()
                for (photoInfo in info) {
                    keywordItems.add(photoInfo.keyword)
                }
                var choice = -1
                val builder = AlertDialog.Builder(fragmentForUse.requireContext()).setIcon(R.mipmap.ic_launcher).setTitle("识别结果")
                    .setSingleChoiceItems(keywordItems.toTypedArray(),0
                    ) { dialog, which -> choice = which }.setPositiveButton("添加"){dialog, which ->
                        if(choice == -1) holder.itemView.camera_card_itemName_editText.setText(keywordItems[0])
                        else holder.itemView.camera_card_itemName_editText.setText(keywordItems[choice])
                    }.setNegativeButton("取消"){dialog, which ->
                        dialog.dismiss()
                    }
                builder.create().show()
            }
            holder.itemView.camera_cardView_TypeInfo.setOnClickListener {
                val keywordItems = mutableListOf<String>()
                for (photoInfo in info) {
                    if (zxzOptionSharedPreferences("typeInfoContainKeyword") != "") keywordItems.add(photoInfo.keyword)
                    keywordItems.add(photoInfo.root)
                }
                var choice = -1
                val builder = AlertDialog.Builder(fragmentForUse.requireContext()).setIcon(R.mipmap.ic_launcher).setTitle("识别结果")
                    .setSingleChoiceItems(keywordItems.toTypedArray(),0
                    ) { dialog, which -> choice = which }.setPositiveButton("添加"){dialog, which ->
                        if (choice == -1) holder.itemView.camera_card_itemType_editText.setText(keywordItems[0])
                        else holder.itemView.camera_card_itemType_editText.setText(keywordItems[choice])
                    }.setNegativeButton("取消"){dialog, which ->
                        dialog.dismiss()
                    }
                builder.create().show()
            }

        }else{
            if (viewModel.showInfoButtonList.value!= null) viewModel.showInfoButtonList.value!![holder.adapterPosition] = false
            Log.d("zxzzxzzxz","添加按钮失败，未获取到信息或信息为空")
        }

    }


    //日期选择框设置
    private fun initDateChooseOptions(holderItem : View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holderItem.apply {
                camera_card_startDate_image.setOnClickListener {
                    val calender = Calendar.getInstance()
                    DatePickerDialog(it.context,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> camera_card_startDate_showText.text = String.format(
                            ZxzStorageApplication.context.resources.getString(R.string.dateShow),year,month,dayOfMonth)
                            camera_card_startDate_editText.setText("${year}${month}${dayOfMonth}")
                        },
                        calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH)).show()
                }
                camera_card_endDate_image.setOnClickListener {
                    val calender = Calendar.getInstance()
                    DatePickerDialog(it.context,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            camera_card_endDate_showText.text = String.format(ZxzStorageApplication.context.resources.getString(R.string.dateShow),year,month,dayOfMonth)
                            camera_card_endDate_editText.setText("${year}${month}${dayOfMonth}")
                        },
                        calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH)).show()
                }
                camera_card_noticeDate_image.setOnClickListener {
                    val calender = Calendar.getInstance()
                    DatePickerDialog(it.context,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> camera_card_noticeDate_showText.text = String.format(
                            ZxzStorageApplication.context.resources.getString(R.string.dateShow),year,month,dayOfMonth)
                            camera_card_noticeDate_editText.setText("${year}${month}${dayOfMonth}")
                        },
                        calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH)).show()
                }
            }
        }else{
            holderItem.apply {
                camera_card_startDate_showText.visibility = View.GONE
                camera_card_startDate_editText.visibility = View.VISIBLE
                // 应该提示一下，放这里会提醒特别多次
                // Toast.makeText(fragmentForUse.context,"安卓版本低，请手动输入，注意日期格式:yyyyMMdd",Toast.LENGTH_SHORT).show()
            }
        }
    }


    //chip设置visible开关
    private fun initChipCheckListener(holderItem : View,position: Int){
        holderItem.apply {
            chip_brand.setOnCheckedChangeListener { buttonView, isChecked ->
                Log.d("zxzzxzzxz",position.toString() + "chip set :" + isChecked)
                viewModel.chipOptionsList.value?.get(position)?.chipOptionBrand = isChecked
                if (isChecked) cardView_Brand.visibility = View.VISIBLE
                else cardView_Brand.visibility = View.GONE
            }
            chip_number.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionNumber = isChecked
                if (isChecked) cardView_Number.visibility = View.VISIBLE
                else cardView_Number.visibility = View.GONE
            }
            chip_price.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionPrice = isChecked
                if (isChecked) cardView_Price.visibility = View.VISIBLE
                else cardView_Price.visibility = View.GONE
            }
            chip_customDescribe.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionCustomDescribe = isChecked
                if (isChecked) cardView_Describe.visibility = View.VISIBLE
                else cardView_Describe.visibility = View.GONE
            }
            chip_startDate.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionStartDate = isChecked
                if (isChecked) {
                    if (cardView_ShelfLife.visibility != View.VISIBLE || cardView_EndDate.visibility != View.VISIBLE)
                        cardView_StartDate.visibility = View.VISIBLE
                    else {
                        chip_startDate.isChecked = false
                        viewModel.chipOptionsList.value?.get(position)?.chipOptionStartDate = false
                        Toast.makeText(this.context,"生产、截止日期与保质期不能同时勾选", Toast.LENGTH_SHORT).show()
                    }
                }
                else cardView_StartDate.visibility = View.GONE
            }
            chip_endDate.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionEndDate = isChecked
                if (isChecked) {
                    if (cardView_ShelfLife.visibility != View.VISIBLE || cardView_StartDate.visibility != View.VISIBLE)
                        cardView_EndDate.visibility = View.VISIBLE
                    else{
                        chip_endDate.isChecked = false
                        viewModel.chipOptionsList.value?.get(position)?.chipOptionEndDate = false
                        Toast.makeText(this.context,"生产、截止日期与保质期不能同时勾选", Toast.LENGTH_SHORT).show()
                    }
                }
                else cardView_EndDate.visibility = View.GONE
            }
            chip_shelfLife.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionShelfLife = isChecked
                if (isChecked) {
                    if (cardView_StartDate.visibility != View.VISIBLE || cardView_EndDate.visibility != View.VISIBLE)
                        cardView_ShelfLife.visibility = View.VISIBLE
                    else{
                        chip_shelfLife.isChecked = false
                        viewModel.chipOptionsList.value?.get(position)?.chipOptionShelfLife = false
                        Toast.makeText(this.context,"生产、截止日期与保质期不能同时勾选", Toast.LENGTH_SHORT).show()
                    }
                }
                else cardView_ShelfLife.visibility = View.GONE
            }
            chip_noticeDate.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionNoticeDate = isChecked
                if (isChecked) cardView_NoticeDate.visibility = View.VISIBLE
                else cardView_NoticeDate.visibility = View.GONE
            }
            chip_noticeContent.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.chipOptionsList.value?.get(position)?.chipOptionNoticeContent = isChecked
                if (isChecked) cardView_NoticeContent.visibility = View.VISIBLE
                else cardView_NoticeContent.visibility = View.GONE
            }
        }
    }


    //文本改变时传viewModel
    private fun initEditTextListener(holderItem: View,position: Int){
        if (viewModel.insertItemList.value == null) return
        else{
                holderItem.apply {
                    camera_card_itemName_editText.addTextChangedListener (mWatcherName)
                    camera_card_itemType_editText.addTextChangedListener (mWatcherType)
                    camera_card_number_editText.addTextChangedListener (mWatcherNumber)
                    camera_card_price_editText.addTextChangedListener (mWatcherPrice)
                    camera_card_brand_editText.addTextChangedListener (mWatcherBrand)
                    camera_card_customDescribe_editText.addTextChangedListener (mWatcherCustomDescribe)
                    camera_card_startDate_editText.addTextChangedListener (mWatcherStartDate)
                    camera_card_endDate_editText.addTextChangedListener (mWatcherEndDate)
                    camera_card_shelfLife_editText.addTextChangedListener (mWatcherShelfLife)
                    camera_card_noticeDate_editText.addTextChangedListener (mWatcherNoticeDate)
                    camera_card_noticeContent_editText.addTextChangedListener (mWatcherNoticeContent)
                }

        }
    }

    override fun onViewDetachedFromWindow(holder: PagerPhotoViewHolder) {
        Log.d("zxzzxzzxz",holder.adapterPosition.toString() + "detach")
        holder.itemView.apply {
            camera_card_itemName_editText.removeTextChangedListener (mWatcherName)
            camera_card_itemType_editText.removeTextChangedListener (mWatcherType)
            camera_card_number_editText.removeTextChangedListener (mWatcherNumber)
            camera_card_price_editText.removeTextChangedListener (mWatcherPrice)
            camera_card_brand_editText.removeTextChangedListener (mWatcherBrand)
            camera_card_customDescribe_editText.removeTextChangedListener (mWatcherCustomDescribe)
            camera_card_startDate_editText.removeTextChangedListener (mWatcherStartDate)
            camera_card_endDate_editText.removeTextChangedListener (mWatcherEndDate)
            camera_card_shelfLife_editText.removeTextChangedListener (mWatcherShelfLife)
            camera_card_noticeDate_editText.removeTextChangedListener (mWatcherNoticeDate)
            camera_card_noticeContent_editText.removeTextChangedListener (mWatcherNoticeContent)
        }
        super.onViewDetachedFromWindow(holder)

    }








    //一大串的TextWatcher
    private val mWatcherName = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].itemName = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherType = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].itemType = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherNumber = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != "") viewModel.insertItemList.value!![currentPosition].itemNumber = s.toString().toInt()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherPrice = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != "") viewModel.insertItemList.value!![currentPosition].itemPrice = s.toString().toFloat()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherBrand = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].brand = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherCustomDescribe = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].customDescribe = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherStartDate = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].startDate = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherEndDate = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].endDate = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherShelfLife = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.shelfLifeList.value!![currentPosition] = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherNoticeDate = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].noticeDate = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    private val mWatcherNoticeContent = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            viewModel.insertItemList.value!![currentPosition].noticeContent = s.toString()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

}

class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)