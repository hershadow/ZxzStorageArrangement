package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import com.zxzlst.zxzstoragearrangement.ui.adapter.SecondAdapter
import kotlinx.android.synthetic.main.second_fragment.*

class SecondFragment : Fragment() {

    companion object {
        fun newInstance() = SecondFragment()
    }

    private lateinit var viewModel: SecondViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.second_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SecondViewModel::class.java)
        //recyclerview
        val layoutManager = GridLayoutManager(activity,3)
        secondFragment_recyclerView.layoutManager = layoutManager
        if(this.context == null) Toast.makeText(ZxzStorageApplication.context,"error-----SecondFragment - 39",Toast.LENGTH_SHORT).show()
        else {
            val adapter = SecondAdapter(this.context!!)
            secondFragment_recyclerView.adapter = adapter
        viewModel.allItemLive.observe(viewLifecycleOwner, Observer {
            //数据库变更后运行此处更新recyclerview
            adapter.setItemList(it)
            adapter.notifyDataSetChanged()
        })
        }

        //测试用
        secondFragment_button.setOnClickListener {
            val item1 = createItem(itemName = "suibianxiede",mainPhotoPath = ZxzStorageApplication.context.externalMediaDirs
                .first().path + "/15889234459327.jpg",brand = "lajipingpai",itemNumber = 5,customDescribe = "dsaaaaaaaaa")
            viewModel.insertItem(item1)
            //viewModel.clearItem()
        }

    }

}
