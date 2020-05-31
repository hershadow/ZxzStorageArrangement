package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.zxzlst.zxzstoragearrangement.MainActivity

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import com.zxzlst.zxzstoragearrangement.ui.adapter.FirstAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.first_fragment.*
import java.lang.Exception

class FirstFragment : Fragment() {

    companion object {
        fun newInstance() = FirstFragment()
    }

    private lateinit var viewModel: FirstViewModel
    private lateinit var adapter : FirstAdapter

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



        //appbar向上收纳的时候隐藏
        firstFragment_AppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener{
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
                if (p0 != null){
                    if (p1 + p0.height == 0){
                        p0.visibility = View.INVISIBLE
                    }else{
                        p0.visibility = View.VISIBLE
                    }
                }
            }
        })




        //recyclerView
        val layoutManager = LinearLayoutManager(requireActivity())
        firstFragment_researchRecyclerView.layoutManager = layoutManager
        adapter = FirstAdapter(this,viewModel.resultList)
        firstFragment_researchRecyclerView.adapter = adapter
        firstFragment_searchEditText.addTextChangedListener{
            val content = it.toString()
            if (content != ""){
                viewModel.searchForResult(content)
                Log.d("zxzzxzzxz","1")
            }else{
                firstFragment_researchRecyclerView.visibility = View.GONE
                viewModel.resultList.clear()
                adapter.notifyDataSetChanged()
                Log.d("zxzzxzzxz","2")
            }
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                firstFragment_researchRecyclerView.visibility = View.VISIBLE
                viewModel.resultList.clear()
                viewModel.resultList.addAll(it)
                adapter.notifyDataSetChanged()
                Log.d("zxzzxzzxz","3")
            }else{
                viewModel.resultList.clear()
                adapter.notifyDataSetChanged()
                firstFragment_researchRecyclerView.visibility = View.GONE
                Toast.makeText(requireContext(),"无匹配物品",Toast.LENGTH_SHORT).show()
                Log.d("zxzzxzzxz","4")
            }
        })




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
