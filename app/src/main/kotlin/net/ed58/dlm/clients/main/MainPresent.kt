package net.ed58.dlm.clients.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.PermissionChecker
import com.wise.common.commonutils.DialogPermissionUtil
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI


/**
 * Created by sunpeng on 16/10/25.
 */

class MainPresent(mainActivity: MainActivity) : BasePresenter<MainPresent.IMainView>() {

    /**
     * FragmentManager对象
     */
    private var mFragmentManager: FragmentManager? = null

    /**
     * FragmentContainer的id
     */
    private var mFragmentContainerId = -1

    private var main_home: MainHomeFragment? = null//第一个Tab
    private var main_order: MainOrderFragment? = null
    private var main_me: MainMeFragment? = null
    private var currentFragment: Fragment? = null


    init {
        mFragmentManager = mainActivity.supportFragmentManager
        mFragmentContainerId = mainActivity.getFragmentContainerId()
    }


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        //判断定位权限是否开启
        val isPermission = PermissionChecker.checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, android.os.Process.myPid(), android.os.Process.myUid(), activity.packageName) == PackageManager.PERMISSION_GRANTED
        if (!isPermission) {
            DialogPermissionUtil.PermissionDialog(activity,"你未允许驿道获取定位权限,您可在系统设置中开启")
        }
    }

    fun showTabHome() {
        val transaction = mFragmentManager!!.beginTransaction()

        hideAllFragmengts(transaction)

        if (main_home == null) {
            main_home = MainHomeFragment()
            transaction.add(mFragmentContainerId, main_home, TAG_TAB_HOME)
        } else {
            transaction.show(main_home)
        }
        currentFragment = main_home
        transaction.commitAllowingStateLoss()
    }

    fun showTabOrder(sendOrderTabPosition: Int) {
        val transaction = mFragmentManager!!.beginTransaction()

        hideAllFragmengts(transaction)

        if (main_order == null) {
            main_order = MainOrderFragment()

            val bundle = Bundle()
            bundle.putInt("sendOrderTabPosition", sendOrderTabPosition)
            main_order!!.arguments = bundle

            transaction.add(mFragmentContainerId, main_order, TAG_TAB_ORDER)
        } else {
            transaction.show(main_order)
        }

        currentFragment = main_order
        transaction.commitAllowingStateLoss()
    }

    fun showTabMe() {
        val transaction = mFragmentManager!!.beginTransaction()

        hideAllFragmengts(transaction)

        if (main_me == null) {
            main_me = MainMeFragment()
            transaction.add(mFragmentContainerId, main_me, TAG_TAB_ME)
        } else {
            transaction.show(main_me)
        }
        currentFragment = main_me
        transaction.commitAllowingStateLoss()
    }

    /**
     * 隐藏所有的Fragment
     */
    private fun hideAllFragmengts(transaction: FragmentTransaction) {

        if (main_home != null) {
            transaction.hide(main_home)
        }
        if (main_order != null) {
            transaction.hide(main_order)
        }
        if (main_me != null) {
            transaction.hide(main_me)
        }

    }

    fun getCurrentFragment() : Fragment? {
        return currentFragment
    }


    interface IMainView : BaseUI {

    }

    companion object {

        private val TAG_TAB_HOME = "tab_home"
        private val TAG_TAB_ORDER = "tab_order"
        private val TAG_TAB_ME = "tab_me"
    }
}
