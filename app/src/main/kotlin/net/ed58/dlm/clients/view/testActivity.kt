package net.ed58.dlm.clients.view

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup

import kotlinx.android.synthetic.main.fragment_me.*

import net.ed58.dlm.clients.adapter.MyAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.main.MainMeFragment

/**
 * Created by Administrator on 2017/11/8/008.
 */

class testActivity : BaseCoreActivity() {
    override fun getLayoutId(): Int {
        var g:ViewGroup
        return  R.layout.fragment_me
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        //设置侧边的按钮
        val item1 = MainMeFragment.Companion.item(R.mipmap.icon_news, "通知", "5");
        val item2 = MainMeFragment.Companion.item(R.mipmap.icon_my_wallet, "我的钱包", "");
        val item3 = MainMeFragment.Companion.item(R.mipmap.icon_invite_friends, "邀请好友", "");
        val item4 = MainMeFragment.Companion.item(R.mipmap.icon_evaluation_records, "评价记录", "");
        val item5 = MainMeFragment.Companion.item(R.mipmap.icon_rule_description, "规则说明", "");
        val item6 = MainMeFragment.Companion.item(R.mipmap.icon_report_complaint, "投诉举报", "");
        val item7 = MainMeFragment.Companion.item(R.mipmap.icon_feedback, "意见反馈", "");
        val item8 = MainMeFragment.Companion.item(R.mipmap.icon_service_hotline, "客服热线", "");
        val item9 = MainMeFragment.Companion.item(R.mipmap.icon_knight_college, "骑士学院", "");
        val item10 = MainMeFragment.Companion.item(R.mipmap.icon_set, "设置", "");
        val item11 = MainMeFragment.Companion.item(0, "设置", "");
        val listItem = listOf<MainMeFragment.Companion.item>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10,item11)
        my_recyclerview.isNestedScrollingEnabled = false
        my_recyclerview.layoutManager = LinearLayoutManager(this)
        my_recyclerview.adapter = MyAdapter(this,R.layout.item_my,listItem);
    }

    override fun onResume() {
        super.onResume()
    }

}
