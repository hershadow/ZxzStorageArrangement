package com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.get
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.adapter.PagerPhotoListAdapter
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.adapter.PagerPhotoViewHolder
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import kotlinx.android.synthetic.main.activity_edit_for_main.*
import kotlinx.android.synthetic.main.camera_bottomsheetdialog.*
import kotlinx.android.synthetic.main.camera_bottomsheetdialog.view.*
import kotlinx.android.synthetic.main.camera_bottomsheetdialog.view.bottomDialog_add_firstChip
import kotlinx.android.synthetic.main.camera_insert_pager_info.view.*
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * 下拉dialog作创建物品的最后步骤
 */
class BottomDialogSheetFragment : BottomSheetDialogFragment() {
    lateinit var behavior: BottomSheetBehavior<FrameLayout>
    val viewModel: CameraInsertViewModel by lazy { ViewModelProviders.of(fragmentForUse).get(CameraInsertViewModel::class.java) }
    var currentChoose : MutableList<Int> = mutableListOf(0,0,0)
    lateinit var holder: PagerPhotoViewHolder
    lateinit var fragmentForUse : Fragment
    lateinit var adapter: PagerPhotoListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.camera_bottomsheetdialog, container, false)
        view.bottomDialog_default_firstChip.setTag(R.id.tag_chip,0)
        view.bottomDialog_default_secondChip.setTag(R.id.tag_chip,0)
        view.bottomDialog_default_thirdChip.setTag(R.id.tag_chip,0)
        return view
    }


    override fun onStart() {
        super.onStart()
            val rootFrameLayout = (dialog as BottomSheetDialog).delegate.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            if (rootFrameLayout != null){
                rootFrameLayout.setBackgroundResource(R.drawable.bottomdialog_color)
                rootFrameLayout.setBackgroundResource(R.drawable.bottomdialog_cornner)
                val params = rootFrameLayout.layoutParams
                val point = Point(1080,1920)
                (context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
                params.height = point.y - 180
                behavior = BottomSheetBehavior.from(rootFrameLayout)
                //behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }


        if (bottomDialog_secondChipGroup.visibility == View.VISIBLE) bottomDialog_secondChipGroup.visibility = View.GONE
        if (bottomDialog_thirdChipGroup.visibility == View.VISIBLE) bottomDialog_thirdChipGroup.visibility = View.GONE

        readyForFirstRoom()
        addAddChipClickListener()
        setChipGroupChecked()
        dialog_confirm_imageButton.setOnClickListener {
            if (viewModel.insertItemList.value?.get(holder.adapterPosition) != null){
                try {
                    viewModel.insertItemList.value?.get(holder.adapterPosition)!!.apply {
                        largeBoxId = currentChoose[0]
                        mediumBoxId = currentChoose[1]
                        smallBoxId = currentChoose[2]
                        categoryId = 0
                        //TODO 此处还需要设置标签
                        var getItemId: Long
                        viewModel.insertItem(this,object : Repository.OnLoadItemIdListener{
                            override fun doAfterGetItemId(returnItemId: Long) {
                                try {
                                    if (returnItemId != (-1).toLong()){
                                        getItemId = returnItemId
                                        if (Repository.repositoryImagePathTemporary.listFiles() != null){
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
                                            Repository.temporaryToNormal(temporaryFiles[holder.adapterPosition],getItemId)

                                        viewModel.insertItemList.value?.get(holder.adapterPosition)!!.id = getItemId
                                        viewModel.insertItemList.value?.get(holder.adapterPosition)!!.mainPhotoPath = Repository.repositoryImagePathNormalMain.path + "/zxz${getItemId}.jpg"
                                        viewModel.updateItem(viewModel.insertItemList.value?.get(holder.adapterPosition)!!)
                                        viewModel.finishOrNotList.value?.set(holder.adapterPosition,true)
                                        requireActivity().runOnUiThread{
                                            adapter.refreshInfoList(2)
                                        }
                                        }
                                    }else dialog_confirm_imageButton.post {
                                        Toast.makeText(
                                            this@BottomDialogSheetFragment.requireContext(),
                                            "录入信息异常",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }catch (e:Exception){
                                    dialog_confirm_imageButton.post {
                                        Toast.makeText(this@BottomDialogSheetFragment.requireContext(),e.toString(),Toast.LENGTH_LONG).show()
                                    }
                                }

                            }
                        })
                    }
                }catch (e:Exception){
                    Log.d("zxzvisible",e.toString())
                }

            }
        }



    }

    private fun readyForFirstRoom(){
        viewModel.loadLargeBoxId(object : Repository.OnLoadItemListListener{
            override fun doAfterGetItemList(returnItemList: List<Item>) {
                for (item in returnItemList){
                    val newChip = layoutInflater.inflate(
                        R.layout.single_chip_layout,
                        bottomDialog_firstChipGroup,
                        false
                    ) as Chip
                    newChip.text = item.itemName
                    newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) readyForSecondRoom(item.largeBoxId)
                    }
                    //给chip加一个tag来拿到该空间的ID
                    newChip.setTag(R.id.tag_chip,item.largeBoxId)
                    bottomDialog_firstChipGroup.post {
                        bottomDialog_firstChipGroup.addView(newChip)
                    }
                }
            }
        })
    }
    private fun readyForSecondRoom(largeBoxId : Int){
        if (bottomDialog_secondChipGroup.childCount > 2) bottomDialog_secondChipGroup.removeViews(2,(bottomDialog_secondChipGroup.childCount-2))
        if (bottomDialog_secondChipGroup.visibility == View.GONE ) bottomDialog_secondChipGroup.visibility = View.VISIBLE
        if (!bottomDialog_default_secondChip.isChecked) bottomDialog_default_secondChip.isChecked = true

        viewModel.loadMediumBoxId(largeBoxId,object : Repository.OnLoadItemListListener{
            override fun doAfterGetItemList(returnItemList: List<Item>) {
                for (item in returnItemList){
                        val newChip = layoutInflater.inflate(
                            R.layout.single_chip_layout,
                            bottomDialog_firstChipGroup,
                            false
                        ) as Chip
                        newChip.text = item.itemName
                        newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                                if (isChecked) readyForThirdRoom(item.mediumBoxId)
                        }
                        //给chip加一个tag来拿到该空间的ID
                        newChip.setTag(R.id.tag_chip,item.mediumBoxId)
                        bottomDialog_secondChipGroup.post {
                            bottomDialog_secondChipGroup.addView(newChip)
                        }
                }

            }
        })
    }
    private fun readyForThirdRoom(mediumBoxId : Int){
            if (bottomDialog_thirdChipGroup.childCount > 2) bottomDialog_thirdChipGroup.removeViews(2,(bottomDialog_thirdChipGroup.childCount-2))
            if (bottomDialog_thirdChipGroup.visibility == View.GONE ) bottomDialog_thirdChipGroup.visibility = View.VISIBLE
            if (!bottomDialog_default_thirdChip.isChecked) bottomDialog_default_thirdChip.isChecked = true
            var largeBoxId : Int = 0
            for (chip in bottomDialog_firstChipGroup.children) if (chip.id == bottomDialog_firstChipGroup.checkedChipId)  largeBoxId = chip.getTag(R.id.tag_chip) as Int
            viewModel.loadSmallBoxId(largeBoxId,mediumBoxId,object : Repository.OnLoadItemListListener{
                override fun doAfterGetItemList(returnItemList: List<Item>) {
                    for (item in returnItemList){
                        val newChip = layoutInflater.inflate(
                            R.layout.single_chip_layout,
                            bottomDialog_firstChipGroup,
                            false
                        ) as Chip
                        newChip.text = item.itemName
                        //给chip加一个tag来拿到该空间的ID
                        newChip.setTag(R.id.tag_chip,item.smallBoxId)
                        bottomDialog_thirdChipGroup.post {
                            bottomDialog_thirdChipGroup.addView(newChip)
                        }

                    }

                }
            })



    }


    //给创建主ROOM添加一个listener
    private fun addAddChipClickListener(){
        //默认chip的listener
        bottomDialog_default_firstChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && bottomDialog_secondChipGroup.visibility == View.VISIBLE )bottomDialog_secondChipGroup.visibility = View.GONE
            if (isChecked &&bottomDialog_thirdChipGroup.visibility == View.VISIBLE)bottomDialog_thirdChipGroup.visibility = View.GONE


        }
        bottomDialog_default_secondChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && bottomDialog_thirdChipGroup.visibility == View.VISIBLE ) bottomDialog_thirdChipGroup.visibility = View.GONE
        }

        //添加chip的listener
        bottomDialog_add_firstChip.setOnClickListener {
            var roomId = -1
            viewModel.getLeisureRoom(1,0,0,object : Repository.OnLoadRoomListener{
                override fun doAfterGetRoom(returnRoom: Int) {
                    roomId = returnRoom
                }
            })
            val editText = EditText(requireContext())
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                .setTitle("输入空间名称").setView(editText).setPositiveButton(
                    "确认创建"
                ) { dialog, which ->
                        val newChip = layoutInflater.inflate(
                            R.layout.single_chip_layout,
                            bottomDialog_firstChipGroup,
                            false
                        ) as Chip
                        newChip.text = editText.text.toString()
                    if (roomId != -1){
                        viewModel.insertItem(createItem(largeBoxId = roomId,mediumBoxId = 0,smallBoxId = 0,itemName = editText.text.toString(),categoryId = 1),null)
                        newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) readyForSecondRoom(roomId)
                        }
                        //给chip加一个tag来拿到该空间的ID
                        newChip.setTag(R.id.tag_chip,roomId)
                        bottomDialog_firstChipGroup.addView(newChip)
                    }
                }.setNegativeButton(
                    "取消"
                ) { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()
        }
        bottomDialog_add_secondChip.setOnClickListener {
            var roomId = -1
            viewModel.getLeisureRoom(2,currentChoose[0],0,object : Repository.OnLoadRoomListener{
                override fun doAfterGetRoom(returnRoom: Int) {
                    roomId = returnRoom
                }
            })
            val editText = EditText(requireContext())
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                .setTitle("输入空间名称").setView(editText).setPositiveButton(
                    "确认创建"
                ) { dialog, which ->
                        val newChip = layoutInflater.inflate(
                            R.layout.single_chip_layout,
                            bottomDialog_secondChipGroup,
                            false
                        ) as Chip
                        newChip.text = editText.text.toString()
                    if (roomId != -1) {
                        viewModel.insertItem(
                            createItem(
                                largeBoxId = currentChoose[0],
                                mediumBoxId = roomId,
                                smallBoxId = 0,
                                itemName = editText.text.toString(),
                                categoryId = 2
                            ), null
                        )
                        newChip.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) readyForThirdRoom(roomId)
                        }
                        //给chip加一个tag来拿到该空间的ID
                        newChip.setTag(R.id.tag_chip, roomId)
                        bottomDialog_secondChipGroup.addView(newChip)
                    }
                }.setNegativeButton(
                    "取消"
                ) { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()
        }
        bottomDialog_add_thirdChip.setOnClickListener {
            var roomId = -1
            viewModel.getLeisureRoom(3,currentChoose[0],currentChoose[1],object : Repository.OnLoadRoomListener{
                override fun doAfterGetRoom(returnRoom: Int) {
                    roomId = returnRoom
                }
            })
            val editText = EditText(requireContext())
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                .setTitle("输入空间名称").setView(editText).setPositiveButton(
                    "确认创建"
                ) { dialog, which ->
                    val newChip = layoutInflater.inflate(
                        R.layout.single_chip_layout,
                        bottomDialog_thirdChipGroup,
                        false
                    ) as Chip
                    newChip.text = editText.text.toString()
                    if (roomId != -1) {
                        viewModel.insertItem(
                            createItem(
                                largeBoxId = currentChoose[0],
                                mediumBoxId = currentChoose[1],
                                smallBoxId = roomId,
                                itemName = editText.text.toString(),
                                categoryId = 3
                            ), null
                        )
                        //给chip加一个tag来拿到该空间的ID
                        newChip.setTag(R.id.tag_chip, roomId)
                        bottomDialog_thirdChipGroup.addView(newChip)
                    }
                }.setNegativeButton(
                    "取消"
                ) { dialog, which ->
                    dialog.dismiss()
                }
            builder.create().show()
        }
    }


    //设置必定选中一个chip ，并且选择标签的时候会
    private fun setChipGroupChecked(){
        bottomDialog_firstChipGroup.setOnCheckedChangeListener { chipGroup, i ->
            var choose = -1
            if (i in 0..1000) for (chip in chipGroup.children) if (chip.id == i) choose = chip.getTag(R.id.tag_chip) as Int
            when{
                choose >= 0 -> currentChoose[0] = choose
                else ->{
                    bottomDialog_default_firstChip.isChecked = true
                    bottomDialog_default_secondChip.isChecked = true
                    currentChoose[0] = 0
                }
            }
        }
        bottomDialog_secondChipGroup.setOnCheckedChangeListener { chipGroup, i ->
                var choose = -1
                if (i in 0..1000) for (chip in chipGroup.children) if (chip.id == i) choose = chip.getTag(R.id.tag_chip) as Int
                when{
                    choose >= 0 -> currentChoose[1] = choose
                    else ->{
                        bottomDialog_default_secondChip.isChecked = true
                        bottomDialog_default_thirdChip.isChecked = true
                        currentChoose[1] = 0
                    }
                }
            Log.d("zxzvisible",currentChoose.toString())
        }
        bottomDialog_thirdChipGroup.setOnCheckedChangeListener { chipGroup, i ->
            var choose = -1
            if (i in 0..1000) for (chip in chipGroup.children) if (chip.id == i) choose = chip.getTag(R.id.tag_chip) as Int
            when{
                choose >= 0 -> currentChoose[2] = choose
                else -> {
                    bottomDialog_default_firstChip.isChecked = true
                    currentChoose[2] = 0
                }
            }
        }
    }

}
