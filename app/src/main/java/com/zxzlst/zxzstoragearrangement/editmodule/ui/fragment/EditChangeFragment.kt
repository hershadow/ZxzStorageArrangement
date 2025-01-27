package com.zxzlst.zxzstoragearrangement.editmodule.ui.fragment

import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.editmodule.EditActivityForMain

class EditChangeFragment : Fragment() {

    companion object {
        fun newInstance() = EditChangeFragment()
    }

    private lateinit var viewModel: EditChangeViewModel

    //同see,获取选择的item
    private var itemId :Long = 0
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        itemId = (activity as EditActivityForMain).recentItemId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_change_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(EditChangeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
