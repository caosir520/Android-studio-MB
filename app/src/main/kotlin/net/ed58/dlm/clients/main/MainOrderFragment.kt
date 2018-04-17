package net.ed58.dlm.clients.main

import android.content.Intent
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wise.common.commonutils.StarusUtil
import kotlinx.android.synthetic.main.fragment_home.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPFragment
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.order.MyReceiveViewPagerFragment
import net.ed58.dlm.clients.order.MySendViewPagerFragment

/**
 * Created by sunpeng on 17/2/10.
 */

class MainOrderFragment : BaseCoreMVPFragment<MainOrderPresent, MainOrderPresent.IMainOrder>(), MainOrderPresent.IMainOrder {

    private var mView: View? = null
    private var myReceiveOrderListFragment: MyReceiveViewPagerFragment? = null
    private var mySendOrderListFragment: MySendViewPagerFragment? = null
    private var sendOrderTabPosition:Int = -1
    override fun createPresenter(): MainOrderPresent {
        return MainOrderPresent()
    }

    override fun getUi(): MainOrderPresent.IMainOrder {
        return this
    }

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_order, container, false)
        }
        return mView!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendOrderTabPosition = arguments.getInt("sendOrderTabPosition")

        radiogroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_send_order->{
                    showSendOrder()
                }
                R.id.rb_receive_order->{
                    showReceivceOrder()
                }
            }
        }


        //注册支付或下单后的监听，改变tab
        mRxManager.on<Int>(RxKey.EVENT_PAY_GO_MAIN_ORDER_TAB) { sendOrderTabPosition ->
            this.sendOrderTabPosition = sendOrderTabPosition
            checkSendTab()
        }
        //查看我的接单列表
        mRxManager.on<Int>(RxKey.EVENT_GO_MY_RECEIVE_ORDER_LIST){sendOrderTabPosition->
            this.sendOrderTabPosition = sendOrderTabPosition
            checkSendTab()
        }
        checkSendTab()
        //强行位移
        StarusUtil.setViewFit(mView,activity)
    }


    /**
     * 选中已发订单的tab
     */
    private fun checkSendTab() {
        when (sendOrderTabPosition) {
            -1, 0, 1 -> {
                radiogroup.check(rb_send_order.id)
            }
            2->{
                radiogroup.check(rb_receive_order.id)
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    fun showReceivceOrder() {
        val transaction = childFragmentManager.beginTransaction()

        hideAllFragmengts(transaction)

        val bundle = Bundle()
        bundle.putInt("sendOrderTabPosition", sendOrderTabPosition)
        if (myReceiveOrderListFragment == null) {
            myReceiveOrderListFragment = MyReceiveViewPagerFragment()
            myReceiveOrderListFragment!!.arguments=bundle
            transaction.add(R.id.container, myReceiveOrderListFragment, "send_order")
        } else {
            transaction.show(myReceiveOrderListFragment)
        }
        transaction.commitAllowingStateLoss()
    }



    fun showSendOrder() {
        val transaction = childFragmentManager.beginTransaction()

        hideAllFragmengts(transaction)

        val bundle = Bundle()
        bundle.putInt("sendOrderTabPosition", sendOrderTabPosition)
        if (mySendOrderListFragment == null) {
            mySendOrderListFragment = MySendViewPagerFragment()

            mySendOrderListFragment!!.arguments = bundle

            transaction.add(R.id.container, mySendOrderListFragment, "receive_order")
        } else {
            transaction.show(mySendOrderListFragment)
        }
        transaction.commitAllowingStateLoss()
    }


    private fun hideAllFragmengts(transaction: FragmentTransaction) {

        if (myReceiveOrderListFragment != null) {
            transaction.hide(myReceiveOrderListFragment)
        }
        if (mySendOrderListFragment != null) {
            transaction.hide(mySendOrderListFragment)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (radiogroup.checkedRadioButtonId) {
            R.id.rb_send_order->{
                mySendOrderListFragment?.onActivityResult(requestCode,resultCode,data)
            }
            R.id.rb_receive_order->{
                myReceiveOrderListFragment?.onActivityResult(requestCode,resultCode,data)
            }
        }
    }

}
