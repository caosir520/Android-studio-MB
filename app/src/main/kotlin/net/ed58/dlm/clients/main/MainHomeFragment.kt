package net.ed58.dlm.clients.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPFragment

/**
 * Created by sunpeng on 17/2/10.
 */

class MainHomeFragment : BaseCoreMVPFragment<MainHomePresent, MainHomePresent.IMainScan>(), MainHomePresent.IMainScan{

    private var mView: View? = null
    private var mainHomeSendOrder: MainHomeSendOrderFragment? = null
    private var mainHomeReceiveOrder: MainHomeReceiveOrderFragment? = null
    private var currentFragment: Fragment? = null

    override fun createPresenter(): MainHomePresent {
        return MainHomePresent()
    }

    override fun getUi(): MainHomePresent.IMainScan {
        return this
    }

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_home, container, false)
        }
        return mView!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        radiogroup.check(rb_send_order.id)
        mView!!.setFitsSystemWindows(true);
    }

    fun showSendOrder() {
        val transaction = childFragmentManager.beginTransaction()

        hideAllFragmengts(transaction)

        if (mainHomeSendOrder == null) {
            mainHomeSendOrder = MainHomeSendOrderFragment()
            transaction.add(R.id.container, mainHomeSendOrder, "send_order")
        } else {
            transaction.show(mainHomeSendOrder)
        }
        currentFragment = mainHomeSendOrder
        transaction.commitAllowingStateLoss()
    }

    fun showReceivceOrder() {
        val transaction = childFragmentManager.beginTransaction()

        hideAllFragmengts(transaction)

        if (mainHomeReceiveOrder == null) {
            mainHomeReceiveOrder = MainHomeReceiveOrderFragment()
            transaction.add(R.id.container, mainHomeReceiveOrder, "receive_order")
        } else {
            transaction.show(mainHomeReceiveOrder)
        }
        currentFragment = mainHomeReceiveOrder
        transaction.commitAllowingStateLoss()
    }

    private fun hideAllFragmengts(transaction: FragmentTransaction) {

        if (mainHomeSendOrder != null) {
            transaction.hide(mainHomeSendOrder)
        }
        if (mainHomeReceiveOrder != null) {
            transaction.hide(mainHomeReceiveOrder)
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onHiddenChanged(hidden: Boolean) {
        if (mView != null) {
            if (hidden) {
                mView!!.setFitsSystemWindows(false);
            } else {
                mView!!.setFitsSystemWindows(true);
            }
            mView!!.requestApplyInsets();
        }
        super.onHiddenChanged(hidden)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }
}
