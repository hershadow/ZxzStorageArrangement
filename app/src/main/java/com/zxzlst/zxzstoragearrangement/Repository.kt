package com.zxzlst.zxzstoragearrangement

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.zxzlst.zxzstoragearrangement.logic.dao.AppDatabase
import com.zxzlst.zxzstoragearrangement.logic.dao.Item
import com.zxzlst.zxzstoragearrangement.logic.dao.createItem
import com.zxzlst.zxzstoragearrangement.logic.filePathToPhotoRequest
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


import com.zxzlst.zxzstoragearrangement.logic.model.PhotoResponse
import com.zxzlst.zxzstoragearrangement.logic.model.ResultInfo
import com.zxzlst.zxzstoragearrangement.logic.model.TokenResponse
import com.zxzlst.zxzstoragearrangement.logic.network.HomeNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException

/**
 * 仓库类
 */
object Repository {
    private val itemDao = AppDatabase.getDatabase(ZxzStorageApplication.context).itemDao()
    var allItemLive : LiveData<List<Item>> = itemDao.loadAllItem()

    //以下是两种不同方法，都行，一种是返回空值，子线程完成了再改，一种是不返回值，子线程拿到东西了再回去执行操作
    private var temporaryItem = createItem()
    private lateinit var temporaryItemList : List<Item>
    private var temporaryItemId : Long = (-1).toLong()

    fun insertItem(item : Item,onLoadItemIdListener: OnLoadItemIdListener?) : Long {
        thread { temporaryItemId = itemDao.insertItem(item)
            if (temporaryItemId != (-1).toLong()) onLoadItemIdListener?.doAfterGetItemId(temporaryItemId)
        }
        return temporaryItemId
    }
    fun deleteItem(item : Item) = thread { itemDao.deleteItem(item) }
    fun updateItem(item : Item) = thread { itemDao.updateItem(item) }
    fun loadItemById(itemId : Long, loadItemListener : OnLoadItemListener) {
        thread {
            temporaryItem = itemDao.loadItemById(itemId)
            loadItemListener.doAfterGetItemById(temporaryItem)
        }
    }
    fun clearAllItem() = thread { itemDao.clearAllItem() }
    fun searchForResult(pattern : String) : LiveData<List<Item>> = itemDao.searchForResult("%$pattern%")

    //回调接口，用于在获取到了item/itemList/itemid之后执行一些绘制等操作
    interface OnLoadItemIdListener {
        fun doAfterGetItemId(returnItemId : Long) = run { }
    }
    interface OnLoadItemListener {
        fun doAfterGetItemById(returnItem : Item) = run { }
    }
    interface OnLoadItemListListener {
        fun doAfterGetItemList(returnItemList : List<Item>) = run { }
    }

    //拿取大中小BOX的ID
    fun loadLargeBoxId(onLoadItemListListener: OnLoadItemListListener) {
        thread { temporaryItemList = itemDao.loadAllLargeBoxType()
            onLoadItemListListener.doAfterGetItemList(temporaryItemList)
        }
    }
    fun loadMediumBoxId(largeBoxId : Int , onLoadItemListListener: OnLoadItemListListener){
        thread { temporaryItemList = itemDao.loadAllMediumBoxType(largeBoxId)
        onLoadItemListListener.doAfterGetItemList(temporaryItemList)}
    }
    fun loadSmallBoxId(largeBoxId : Int ,mediumBoxId : Int ,onLoadItemListListener: OnLoadItemListListener){
        thread { temporaryItemList = itemDao.loadAllSmallBoxType(largeBoxId,mediumBoxId)
            onLoadItemListListener.doAfterGetItemList(temporaryItemList)}
    }







        // 创建主层空间
        fun createLargeRoom(string : String) : Int{
            //TODO         这里要做的是  查找数据库的主空间序号，创建一个与已存在的空间序号不同的新空间，返回该空间序号
            return 0
        }




        //insert 用的格式转换
        val insertDateFormat : SimpleDateFormat = SimpleDateFormat("yyyyMMdd",Locale.CHINA)

        //下面是关于图片的管理方法
        val repositoryImagePathNormal : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/normal")
        val repositoryImagePathMipMap : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/mipmap")
        val repositoryImagePathTemporary : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/temporary")
        val repositoryImagePathTemporaryMipmap : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/temporarymipmap")
        val repositoryImagePathTrash : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/trash")
        val repositoryImagePathNormalMain : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/normal/main")
        val repositoryImagePathMipmapMain : File = File(ZxzStorageApplication.context.externalMediaDirs[0].path + "/mipmap/main")
        //对软件的图片文件夹进行初始化
        fun initImageFileArrangement(){
            if (!repositoryImagePathNormal.exists()) repositoryImagePathNormal.mkdir()
            if (!repositoryImagePathMipMap.exists()) repositoryImagePathMipMap.mkdir()
            if (!repositoryImagePathTemporary.exists()) repositoryImagePathTemporary.mkdir()
            if (!repositoryImagePathTemporaryMipmap.exists()) repositoryImagePathTemporaryMipmap.mkdir()
            if (!repositoryImagePathTrash.exists()) repositoryImagePathTrash.mkdir()
            if (!repositoryImagePathNormalMain.exists()) repositoryImagePathNormalMain.mkdir()
            if (!repositoryImagePathMipmapMain.exists()) repositoryImagePathMipmapMain.mkdir()
        }
        //拍照后处理照片，旋转的放trash里，正确的照片放在temporary里面
        private val theManager = ZxzStorageApplication.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        fun trashToTemporary(imageFile : File) : String{
            val date = getDateForFileName()
            if (zxzOptionSharedPreferences("takingPhotoRotate") != "") {
                val mBitmap = BitmapFactory.decodeFile(imageFile.path)
                val fileOut = FileOutputStream(File(repositoryImagePathTemporary.path + "/$date.jpg"))
                val matrix = Matrix()
                matrix.postRotate(
                    when (theManager.defaultDisplay.rotation) {
                        0 -> 90F
                        1 -> 0F
                        2 -> 270F
                        3 -> 180F
                        else -> 0F
                    }, mBitmap.width / 2.toFloat(), mBitmap.height / 2.toFloat()
                )
                val newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.width, mBitmap.height, matrix, true)
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut)
                fileOut.use { it.flush() }
                imageFile.renameTo(File(repositoryImagePathTrash.path + "/$date.jpg"))
            }else imageFile.renameTo(File(repositoryImagePathTemporary.path + "/$date.jpg"))

            val mmBitmap = BitmapFactory.decodeFile(repositoryImagePathTemporary.path + "/$date.jpg")
            val mFileOut = FileOutputStream(File(repositoryImagePathTemporaryMipmap.path + "/$date.jpg"))
            val matrix = Matrix()
            matrix.postScale(0.3F,0.3F,mmBitmap.width/2.toFloat(),mmBitmap.height/2.toFloat())
            val newBitmap = Bitmap.createBitmap(mmBitmap, 0, 0, mmBitmap.width, mmBitmap.height, matrix, true)
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, mFileOut)
            mFileOut.use { it.flush() }
            return date
        }
        //获取当前时间，返回字符串
        private fun getDateForFileName():String = SimpleDateFormat("yyyyMMddHH_mm_ssSSS", Locale.CHINA).format(Date(System.currentTimeMillis()))
        //拍照后准备保存信息时对图像的操作  拿到id后将图片一起发过来，该方法将其改名储存起来。
        fun temporaryToNormal(imageFile: File,itemId : Long){
            val mBitmap = BitmapFactory.decodeFile(imageFile.path)
            val fileOut = FileOutputStream(File(repositoryImagePathMipmapMain.path + "/zxz${itemId}.jpg"))
            val matrix = Matrix()
            matrix.postScale(0.3F,0.3F,mBitmap.width/2.toFloat(),mBitmap.height/2.toFloat())
            val newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.width, mBitmap.height, matrix, true)
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOut)
            fileOut.use { it.flush() }
            imageFile.renameTo(File(repositoryImagePathNormalMain.path + "/zxz${itemId}.jpg"))
        }

    // 把item的mainPhotoPath传进来，得到mipmap的地址
    fun mainPhotoPathToMipMap(photoPath :String ):String{
        return if (File(Repository.repositoryImagePathMipmapMain.path + "/" + File(photoPath).name).exists())
            Repository.repositoryImagePathMipmapMain.path + "/" + File(photoPath).name
        else photoPath
    }



    //网络工具方法
    //Token方法，返回livedata
    fun searchToken(ak:String, sk:String) = liveData(Dispatchers.IO){
        val result = try {
            val tokenResponse = HomeNetwork.searchToken(ak,sk)
            if(tokenResponse.expires_in.length >= 3){
                val token = tokenResponse.access_token
                Result.success(token)
                //Log.d("zxzzxz","I GOT the token : ${token}")
            }else{
                //Log.d("zxzzxz","response Token error ${tokenResponse.refresh_token} & ${tokenResponse.expires_in}")
                Result.failure<List<TokenResponse>>(RuntimeException("response Token error ${tokenResponse.refresh_token} & ${tokenResponse.expires_in}"))
            }
        }catch (e:Exception){
            Result.failure<List<TokenResponse>>(e)
            //Log.d("zxzzxz",e.toString())
        }
        emit(result)
    }

    //Photo方法，返回livedata
    fun searchPhoto(token:String,filePath : String) = liveData(Dispatchers.IO){
        val result = try {
            val photoResponse = HomeNetwork.searchPhoto(token, filePathToPhotoRequest(filePath))
            if (photoResponse.result_num != ""){
                val photoResult = photoResponse.result
                Result.success(photoResult)
            }else{
                Result.failure<List<PhotoResponse>>(RuntimeException("zxz异常：收到的photo结果为${photoResponse.result_num}"))
            }
        }catch (e:Exception){
            Result.failure<List<PhotoResponse>>(e)
        }
        emit(result)
    }
}





