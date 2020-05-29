package com.zxzlst.zxzstoragearrangement.editmodule

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zxzlst.zxzstoragearrangement.R
import com.zxzlst.zxzstoragearrangement.editmodule.ui.fragment.EditSeeFragment
import kotlinx.android.synthetic.main.activity_edit_for_main.*
import kotlinx.android.synthetic.main.activity_main.*

class EditActivityForMain : AppCompatActivity() {
    val recentItemId : Long  by lazy { intent.extras?.get("itemId") as Long }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_for_main)

        //待处理，判断传入的操作模式来判断是选择那个fragment

    }


    //调用此activity的方法，传入俩参数，一个是item的id，一个是操作模式。分为查看和修改
    companion object{
        fun activityStart(context : Context,itemId : Long , runningType : Int){
            val intent = Intent(context,EditActivityForMain::class.java)
            intent.putExtra("itemId",itemId)
            intent.putExtra("runningType",runningType)
            context.startActivity(intent)
        }
    }
}
