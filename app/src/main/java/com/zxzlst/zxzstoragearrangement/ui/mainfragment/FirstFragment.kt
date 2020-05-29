package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import com.zxzlst.zxzstoragearrangement.MainActivity

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.insertmodule.CameraActivity
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.first_fragment.*

class FirstFragment : Fragment() {

    companion object {
        fun newInstance() = FirstFragment()
    }

    private lateinit var viewModel: FirstViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.first_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FirstViewModel::class.java)
        (requireActivity() as MainActivity).supportActionBar?.hide()

        //测试用
        justForTest_button.setOnClickListener {
            Repository.insertItem(createItem(largeBoxId = 1,categoryId = 1,itemName = "第一个大盒子ID1"),null)
            Repository.insertItem(createItem(largeBoxId = 2,categoryId = 1,itemName = "第二个大盒子ID2"),null)
            Repository.insertItem(createItem(largeBoxId = 1,mediumBoxId = 1,categoryId = 2,itemName = "第一个中盒子大ID1的中ID1"),null)
            Repository.insertItem(createItem(largeBoxId = 2,mediumBoxId = 1,categoryId = 2,itemName = "第二个中盒子大ID2的中ID1"),null)
        }


        // TODO: Use the ViewModel
    }

}
