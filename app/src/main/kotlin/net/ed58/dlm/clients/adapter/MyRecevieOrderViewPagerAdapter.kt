package net.ed58.dlm.clients.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import net.ed58.dlm.clients.order.MyReceiveOrderListFragment
import net.ed58.dlm.clients.order.MySendOrderListFragment

/**
 *接受订单的ViewPager的适配器
 */
class MyRecevieOrderViewPagerAdapter(fm: FragmentManager, private val categorys: List<String>) : FragmentPagerAdapter(fm) {
    var currentFragment: MyReceiveOrderListFragment? = null
         private set
    override fun getItem(index: Int): Fragment {
        val fragment = MyReceiveOrderListFragment()
        val bundle = Bundle()
        bundle.putInt("index", index)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return categorys.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return categorys[position]
    }


    override fun getPageWidth(position: Int): Float {
        return super.getPageWidth(position)
    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, `object`: Any) {
        currentFragment = `object` as MyReceiveOrderListFragment
        super.setPrimaryItem(container, position, `object`)
    }

}