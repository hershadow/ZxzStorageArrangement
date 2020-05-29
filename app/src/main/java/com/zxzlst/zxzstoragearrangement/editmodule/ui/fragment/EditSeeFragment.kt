package com.zxzlst.zxzstoragearrangement.editmodule.ui.fragment

import android.app.Activity
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.editmodule.EditActivityForMain
import com.zxzlst.zxzstoragearrangement.editmodule.ui.adapter.EditInfoAdapter
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import kotlinx.android.synthetic.main.edit_see_fragment.*
import java.lang.Exception

class EditSeeFragment : Fragment() {
    companion object {
        fun newInstance() = EditSeeFragment()
    }

    private lateinit var viewModel: EditSeeViewModel

    //获得当前的ItemId
    private var itemId :Long = 0
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        itemId = (activity as EditActivityForMain).recentItemId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_see_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

            viewModel = ViewModelProviders.of(this).get(EditSeeViewModel::class.java)
            var recentItem: Item?
            viewModel.loadItemById(itemId , object : Repository.OnLoadItemListener{
                override fun doAfterGetItemById(returnItem: Item) {
                        recentItem = returnItem
                        requireActivity().runOnUiThread {
                            if (recentItem?.mainPhotoPath != null) editSee_imageView.setImageBitmap(BitmapFactory.decodeFile(Repository.mainPhotoPathToMipMap(
                                recentItem?.mainPhotoPath!!
                            )))
                            val infoLayoutManager = LinearLayoutManager(requireActivity())
                            val infoAdapter = EditInfoAdapter(requireActivity() as EditActivityForMain, recentItem!!)
                            editSee_textViewLeft.text = recentItem?.itemName
                            editSee_textViewRight.text = recentItem?.itemType
                            editSee_recyclerViewInfo.layoutManager = infoLayoutManager
                            editSee_recyclerViewInfo.adapter = infoAdapter
                        }
                    //副图先暂时搁置
                }
            })



        // TODO: Use the ViewModel
    }

}
