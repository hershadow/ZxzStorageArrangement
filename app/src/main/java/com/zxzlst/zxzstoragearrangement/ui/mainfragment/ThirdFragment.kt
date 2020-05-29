package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import kotlinx.android.synthetic.main.third_fragment.*

class ThirdFragment : Fragment() {

    companion object {
        fun newInstance() = ThirdFragment()
    }

    private lateinit var viewModel: ThirdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.third_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ThirdViewModel::class.java)


        //测试用
        thirdFragment_test.setOnClickListener {
            Repository.insertItem(createItem(largeBoxId = 1,categoryId = 1,itemName = "第一个大盒子ID1"),null)
            Repository.insertItem(createItem(largeBoxId = 2,categoryId = 1,itemName = "第二个大盒子ID2"),null)
            Repository.insertItem(createItem(largeBoxId = 1,mediumBoxId = 1,categoryId = 2,itemName = "第一个中盒子大ID1的中ID1"),null)
            Repository.insertItem(createItem(largeBoxId = 2,mediumBoxId = 1,categoryId = 2,itemName = "第二个中盒子大ID2的中ID1"),null)
        }



        // TODO: Use the ViewModel
    }

}
