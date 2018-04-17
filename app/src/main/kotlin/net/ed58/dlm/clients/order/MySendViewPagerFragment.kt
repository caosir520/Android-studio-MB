package net.ed58.dlm.clients.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wise.common.commonutils.DisplayUtil
import kotlinx.android.synthetic.main.fragment_my_send_viewpager.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.MySendOrderViewPagerAdapter
import net.ed58.dlm.clients.base.BaseCoreFragment
import net.ed58.dlm.clients.global.RxKey
import java.util.*

/**
 * Created by sunpeng on 17/11/7.
 */

class MySendViewPagerFragment : BaseCoreFragment(){

    private var mView: View? = null
    private var mAdapter: MySendOrderViewPagerAdapter? = null
    private var sendOrderTabPosition:Int = 0


    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_my_send_viewpager, container, false)
        }
        return mView!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        sendOrderTabPosition = arguments.getInt("sendOrderTabPosition")

        super.onViewCreated(view, savedInstanceState)
        front_tabPage.tabPaddingLeftRight = DisplayUtil.dip2px(15f)//左右间距
        front_tabPage.textSize = 15
        front_tabPage.shouldExpand = true
        front_tabPage.setTextColorResource(R.color.fuzhu_text_color)
        front_tabPage.selectTextColor = resources.getColor(R.color.main_color)
        front_viewpager.setPadding(0, DisplayUtil.dip2px(41f), 0, 0)

        val list = ArrayList<String>()
        list.add("待支付")
        list.add("待接单")
        list.add("已接单")
        list.add("更多")
        mAdapter = MySendOrderViewPagerAdapter(childFragmentManager, list)
        front_viewpager.offscreenPageLimit = 1
        front_viewpager.adapter = mAdapter
        front_tabPage.setViewPager(front_viewpager)

        front_viewpager.currentItem = sendOrderTabPosition

        //注册支付或下单后的监听，改变tab
        mRxManager.on<Int>(RxKey.EVENT_PAY_GO_MAIN_ORDER_TAB) { sendOrderTabPosition ->
            if (sendOrderTabPosition == front_viewpager.currentItem) {
                //假如正好在这个fragment 就让他主动加载
                mAdapter?.currentFragment?.lazyLoad()
            }else{
                front_viewpager.currentItem = sendOrderTabPosition
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mAdapter?.currentFragment?.onActivityResult(requestCode,resultCode,data)
    }


}