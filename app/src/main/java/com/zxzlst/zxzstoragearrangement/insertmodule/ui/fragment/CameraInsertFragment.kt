package com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment

import android.opengl.Visibility
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.transition.VisibilityPropagation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.getTemporaryFiles
import com.zxzlst.zxzstoragearrangement.insertmodule.ui.adapter.PagerPhotoListAdapter
import com.zxzlst.zxzstoragearrangement.logic.model.ResultInfo
import kotlinx.android.synthetic.main.camera_insert_fragment.*
import kotlinx.android.synthetic.main.camera_insert_pager_info.*
import java.io.File
import java.lang.Exception
import java.util.*

class CameraInsertFragment : Fragment() {

    companion object {
        fun newInstance() = CameraInsertFragment()
    }

    private lateinit var viewModel: CameraInsertViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.camera_insert_fragment, container, false)
    }

    //对上了标记的文件进行清除
    override fun onDestroyView() {
        if (viewModel.deleteList.size != 0){
            var deleteNum = 0
            for (file in viewModel.deleteList){
                if (file.delete()) deleteNum += 1
                File(Repository.repositoryImagePathTemporaryMipmap.path + file.name).apply {
                    if (exists()) delete()
                }
            }
            if (deleteNum == viewModel.deleteList.size) Toast.makeText(requireContext(), "全部删除，共删除 $deleteNum 项",Toast.LENGTH_SHORT).show()
            else Toast.makeText(requireContext(), "未完全删除，共删除 $deleteNum / ${viewModel.deleteList.size} 项",Toast.LENGTH_SHORT).show()
        }
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CameraInsertViewModel::class.java)



        //返回按钮功能设置
        val navController = Navigation.findNavController(cameraInsert_return)
        cameraInsert_return.setOnClickListener {
            navController.navigate(R.id.action_cameraInsertFragment_to_cameraTakingFragment)
        }





        //pagerView滚动屏幕设置     文件夹先按最后修改日期排好序
            viewModel.initViewModelData(getTemporaryFiles())


            //获得相机拍摄后的识图结果，从这里传进来能减少一部分调用时间 （也能减少一些流量开销）
            if (arguments!= null){
                val gson = Gson()
                val jsonGet = arguments!!.getString("photoList")
                viewModel.token = arguments!!.getString("token").toString()
                val photoList = gson.fromJson(jsonGet,object : TypeToken<List<List<ResultInfo>>>(){}.type) as MutableList<MutableList<ResultInfo>>
                if (photoList.size > viewModel.photoResearchList.value?.size ?: 0) Toast.makeText(requireContext(),"图片数量异常",Toast.LENGTH_SHORT).show()
                else{
                    //先拿了，调整完在放回viewModel ，减少内存消耗，避免（可能的）多次调用observer
                    val photoInfoList =  viewModel.photoResearchList.value!!
                    for (photoInfoIndex in 0 until photoList.size){
                        photoInfoList[photoInfoList.size - photoList.size + photoInfoIndex] = photoList[photoInfoIndex]
                    }
                    viewModel.photoResearchList.value = photoInfoList
                }
            }


            val pagerPhotoListAdapter = PagerPhotoListAdapter(this)
            pagerPhotoListAdapter.apply {
                viewPager2.adapter = this
                submitList(getTemporaryFiles().toList())
            }

            //刷新按钮设置事件，该操作网络传输要耗时
            cameraInsert_refresh.setOnClickListener {
                if (viewModel.token == ""){
                    Toast.makeText(requireContext(),"未获取到token无法查询",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                //TODO   此处要发送请求。待写         在OBSERVER中receiveInfoList（1）来通知刷新结果



            }

            //设置识图结果LIST发生改变后，调用adapter的方法
            viewModel.photoResearchList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                pagerPhotoListAdapter.refreshInfoList(1)
            })





        // TODO: Use the ViewModel

    }



}
