package com.zxzlst.zxzstoragearrangement.insertmodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zxzlst.zxzstoragearrangement.R
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Size
import android.graphics.Matrix
import android.graphics.Point
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.camera.core.*
import androidx.camera.view.TextureViewMeteringPointFactory
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.zxzlst.zxzstoragearrangement.REQUEST_CODE_PERMISSIONS
import com.zxzlst.zxzstoragearrangement.ZxzStorageApplication
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.Executors



class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportActionBar?.hide()
    }


    //暂时putExtra没什么用。考虑是否删除 ,可以设置如果有缓存照片，可以设置runningtype直接操作缓存的
    companion object{
        fun activityStart(context : Context,runningType : Int){
            /*       好像context 转 activity有问题
            if (arrayOf(Manifest.permission.CAMERA).all {
                    ContextCompat.checkSelfPermission(
                        ZxzStorageApplication.context, it) == PackageManager.PERMISSION_GRANTED
                }){
                val intent = Intent(context,CameraActivity::class.java)
                intent.putExtra("cameraType",runningType)
                context.startActivity(intent)
            }else{
                val nowActivity = when(context){
                    is ContextWrapper -> context.baseContext as Activity
                    else -> context as Activity
                }
                ActivityCompat.requestPermissions(
                    nowActivity, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
            }

             */

            val intent = Intent(context,CameraActivity::class.java)
            intent.putExtra("cameraType",runningType)
            context.startActivity(intent)
        }
    }



    //返回策略
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this,R.id.fragmentHost_camera)
        if (navController.currentDestination?.id ?: 0 == R.id.cameraInsertFragment){
            navController.navigate(R.id.action_cameraInsertFragment_to_cameraTakingFragment)
        }else finish()
        //super.onBackPressed()
    }










}
