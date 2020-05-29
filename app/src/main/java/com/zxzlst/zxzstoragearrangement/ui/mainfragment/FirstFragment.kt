package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.insertmodule.CameraActivity
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


        //该textView仅作为测试用
        //justForTest_textView.text = String.format("%08d",Repository.getCalendarForFile())
        justForTest_textView.text = ""
        justForTest_button.setOnClickListener {
            CameraActivity.activityStart(this.context!!,0)
            //Repository.clearAllItem()
        }


        // TODO: Use the ViewModel
    }

}
