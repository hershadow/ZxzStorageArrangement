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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zxzlst.zxzstoragearrangement.MainActivity

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.insertmodule.CameraActivity
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import com.zxzlst.zxzstoragearrangement.ui.adapter.FirstAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.first_fragment.*

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

        //recyclerView
        val layoutManager = LinearLayoutManager(requireActivity())
        firstFragment_researchRecyclerView.layoutManager = layoutManager
        adapter = FirstAdapter(this,viewModel.resultList)
        firstFragment_researchRecyclerView.adapter = adapter
        firstFragment_searchEditText.addTextChangedListener{
            if (it != null){
                val content = it.toString()
                viewModel.searchForResult(content)
            }else{
                firstFragment_researchRecyclerView.visibility = View.GONE
                viewModel.resultList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null){
                firstFragment_researchRecyclerView.visibility = View.VISIBLE
                viewModel.resultList.clear()
                viewModel.resultList.addAll(it)
                adapter.notifyDataSetChanged()
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
