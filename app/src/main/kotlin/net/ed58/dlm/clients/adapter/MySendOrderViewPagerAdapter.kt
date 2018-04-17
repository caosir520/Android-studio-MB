package net.ed58.dlm.clients.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

import net.ed58.dlm.clients.order.MySendOrderListFragment

/**
 *
 */
class MySendOrderViewPagerAdapter(fm: FragmentManager, private val categorys: List<String>) : FragmentPagerAdapter(fm) {
    var currentFragment: MySendOrderListFragment? = null

    override fun getItem(index: Int): Fragment {
        val fragment = MySendOrderListFragment()
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
        currentFragment = `object` as MySendOrderListFragment
        super.setPrimaryItem(container, position, `object`)
    }

}
