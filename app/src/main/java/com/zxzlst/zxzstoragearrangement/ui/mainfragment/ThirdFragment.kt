package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.insertmodule.CameraActivity
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


        //该textView仅作为测试用
        //justForTest_textView.text = String.format("%08d",Repository.getCalendarForFile())
        thirdFragment_test.setOnClickListener {
            CameraActivity.activityStart(this.context!!,0)
            //Repository.clearAllItem()
        }






        // TODO: Use the ViewModel
    }

}
