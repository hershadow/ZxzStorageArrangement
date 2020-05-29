package com.zxzlst.zxzstoragearrangement.insertmodule.ui.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zxzlst.zxzstoragearrangement.R
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Size
import android.graphics.Matrix
import android.graphics.Point
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.*
import androidx.camera.core.*
import androidx.camera.view.TextureViewMeteringPointFactory
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.zxzlst.zxzstoragearrangement.REQUEST_CODE_PERMISSIONS
import com.zxzlst.zxzstoragearrangement.Repository
import com.zxzlst.zxzstoragearrangement.logic.model.ResultInfo
import kotlinx.android.synthetic.main.camera_taking_fragment.*
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.util.concurrent.Executors


class CameraTakingFragment : Fragment() {
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    companion object {
        fun newInstance() = CameraTakingFragment()
    }

    private lateinit var viewModel: CameraTakingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.camera_taking_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CameraTakingViewModel::class.java)
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //ak , sk 暂定是这里直接设置
            viewModel.searchToken("MbZGLSjwX5WTQ22mG2e4FLQy","cObaQ0hILVeQcejRY87Gca8pyozoZGPs")

            val navController = Navigation.findNavController(camera_imageView)
            //camera_info_textView.text = navController.currentDestination?.id.toString()
            //camera_number_textView.text = R.id.cameraTakingFragment.toString()
            //设置待处理图片数量初值
            viewModel.setPhotoNumber((Repository.repositoryImagePathTemporary.list() as Array<*>).size)
            //TODO 为解决问题：返回时清除了文件，但是（估计是因为清除文件是个耗时操作），这里不能及时的更新数字


            viewModel.photoNumber.observe(viewLifecycleOwner,Observer {
                camera_number_textView.text = it.toString()
            })

            //网络请求的结果的observer
            viewModel.tokenLiveData.observe(viewLifecycleOwner, Observer {
                val returnToken = it.getOrNull()
                if (returnToken != null) viewModel.token = returnToken as String
                else Toast.makeText(requireContext(),"未获取到TOKEN，可能是网络问题",Toast.LENGTH_SHORT).show()
            })
            viewModel.photoLiveData.observe(viewLifecycleOwner, Observer {
                val returnPhoto = it.getOrNull()
                if (returnPhoto != null){
                    viewModel.photoList.add(returnPhoto as List<ResultInfo>)
                    var infoText : String = returnPhoto[0].keyword
                    for (result in returnPhoto) if (!result.equals(returnPhoto[1])) infoText = infoText + "\n" + result.keyword
                    camera_info_textView.text = infoText
                }
            })

        //相册的点击事件，进入insertFragment，同时带过去photoList
            camera_imageView.setOnClickListener {
                if ((Repository.repositoryImagePathTemporary.list() as Array<*>).isNotEmpty()){
                    val bundle = Bundle()
                    val gson = Gson()
                    val jsonPhotoList : String = gson.toJson(viewModel.photoList)
                    bundle.putString("photoList",jsonPhotoList)
                    bundle.putString("token",viewModel.token)
                    navController.navigate(R.id.action_cameraTakingFragment_to_cameraInsertFragment,bundle)
                }
            }


            viewFinder = camera_textureView
            // Request camera permissions
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }

            // Every time the provided texture view changes, recompute layout
            viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTransform()
            }




        // TODO: Use the ViewModel
    }







    private var textureOutSize = Size(4,3)  //属性和宽高是反的，实际上width是高 ， height是宽，这是因为手机相机有90°倾角
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(requireActivity(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun startCamera() {


            // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(1920,1080))
            setLensFacing(CameraX.LensFacing.BACK)
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)
            viewFinder.setOnTouchListener { _, event ->
                val pointFactory = TextureViewMeteringPointFactory(viewFinder)
                val meteringPoint = pointFactory.createPoint(event?.x ?: 0F,event?.y ?: 0F)
                val action = FocusMeteringAction.Builder.from(meteringPoint).build()
                CameraX.getCameraControl(CameraX.LensFacing.BACK).startFocusAndMetering(action)
                true
            }
            viewFinder.surfaceTexture = it.surfaceTexture
            textureOutSize = it.textureSize
            updateTransform()
            //Log.d("zxzzxz" , previewConfig.targetResolution.toString())
        }

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                setLensFacing(CameraX.LensFacing.BACK)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        camera_capture_button.setOnClickListener {
            val file = File(requireActivity().externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(requireActivity().baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val newFileName = Repository.trashToTemporary(file)
                        val msg = "获取到照片: ${file.name}"

                        try {
                            requireActivity().runOnUiThread {
                                viewModel.setPhotoNumber((Repository.repositoryImagePathTemporary.list() as Array<*>).size)
                                if (viewModel.token != "")
                                    viewModel.searchPhoto(viewModel.token,Repository.repositoryImagePathTemporaryMipmap.path + "/$newFileName.jpg")
                            }
                        }catch (e:Exception){
                            Log.d("zxzzxzzxz",e.toString())
                        }

                        viewFinder.post {
                            Toast.makeText(requireActivity().baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, imageCapture)



    }
    private fun updateTransform() {

        setTextureSize()

        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private  fun setTextureSize(){
        val realSize = Point(0,0)
        requireActivity().windowManager.defaultDisplay.getSize(realSize)
        val savedHeight = 0.8F * realSize.y
        //竖屏情况，且默认4：3，横屏的时候if oritation ，作相应更改
        if (savedHeight > realSize.x.toFloat() * textureOutSize.width / textureOutSize.height){
            cameraGuidelineLeft.setGuidelinePercent(0F)
            cameraGuidelineRight.setGuidelinePercent(1F)
            cameraGuidelineTop.setGuidelinePercent(0F)
            cameraGuidelineBottom.setGuidelinePercent((realSize.x.toFloat() * textureOutSize.width / textureOutSize.height) / realSize.y.toFloat())
        }else{
            cameraGuidelineLeft.setGuidelinePercent(0.5F - ((realSize.y).toFloat() * textureOutSize.height / textureOutSize.width / 2) / realSize.x.toFloat() )
            cameraGuidelineRight.setGuidelinePercent(0.5F + ((realSize.y).toFloat() * textureOutSize.height / textureOutSize.width / 2) / realSize.x.toFloat() )
            cameraGuidelineTop.setGuidelinePercent(0F)
            cameraGuidelineBottom.setGuidelinePercent(0.8F)
        }
    }

}
