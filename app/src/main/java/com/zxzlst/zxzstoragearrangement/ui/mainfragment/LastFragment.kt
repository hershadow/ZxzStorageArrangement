package com.zxzlst.zxzstoragearrangement.ui.mainfragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zxzlst.zxzstoragearrangement.MainActivity

import com.zxzlst.zxzstoragearrangement.R
import kotlinx.android.synthetic.main.activity_main.*

class LastFragment : Fragment() {

    companion object {
        fun newInstance() = LastFragment()
    }

    private lateinit var viewModel: LastViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.last_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LastViewModel::class.java)
        (requireActivity() as MainActivity).supportActionBar?.show()
        // TODO: Use the ViewModel
    }

}
