package net.ed58.dlm.clients.main

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aspsine.irecyclerview.OnLoadMoreListener
import com.aspsine.irecyclerview.OnRefreshListener
import com.aspsine.irecyclerview.widget.LoadMoreFooterView
import com.wise.common.commonutils.CollectionUtils
import kotlinx.android.synthetic.main.layout_auth.*
import kotlinx.android.synthetic.main.layout_receive_list.*
import net.ed58.dlm.clients.adapter.ReceiveListAdapter
import net.ed58.dlm.clients.base.BaseCoreMVPFragment
import net.ed58.dlm.clients.entity.CustomerBean
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.order.AuthActivity

/**
 * Created by sunpeng on 17/11/7.
 */

class MainHomeReceiveOrderFragment : BaseCoreMVPFragment<MainHomeReceivePresent, MainHomeReceivePresent.IMainHomeReceive>(), MainHomeReceivePresent.IMainHomeReceive, OnRefreshListener, OnLoadMoreListener {


    private lateinit var adapter: ReceiveListAdapter
    var authSuccess: Boolean = false

    private var mView: View? = null

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_receive_order, container, false)
        }
        return mView!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.loadMoreFooterView.visibility = View.GONE
        recyclerView.setOnLoadMoreListener(this)
        recyclerView.setOnRefreshListener(this)
        adapter = ReceiveListAdapter(activity)
        recyclerView.adapter = adapter
        button_pay.setOnClickListener {
            AuthActivity.startActivity(activity)
        }
    }


    override fun getUi(): MainHomeReceivePresent.IMainHomeReceive {
        return this
    }

    override fun createPresenter(): MainHomeReceivePresent {
        return MainHomeReceivePresent()
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (isPageVisible) {
            requestByEnter()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPageVisible) {
            requestByEnter()
        }
    }


    //一进去就请求
    private fun requestByEnter() {
        if (authSuccess) {
            //认证成功后 每次定位重新请求
            presenter.requestList()
        } else {
            presenter.requestAuth()
        }
    }


    override fun onAuthSuccess(customerBean: CustomerBean?) {
        when (customerBean?.authStatus) {
            -1 -> {
                //审核失败
                layout_auth.visibility = View.VISIBLE
                layout_list.visibility = View.GONE
                isVisible = false
                text_title.text = "实名认证失败"
                text_content.text = "请重新提交！"
            }
            0 -> {
                //尚未提交审核
                layout_auth.visibility = View.VISIBLE
                layout_list.visibility = View.GONE
                isVisible = false
                text_title.text = "请完成实名认证"
                text_content.text = "你需要提供身份证照片等信息"
            }
            1 -> {
                //待审核
                layout_auth.visibility = View.VISIBLE
                layout_list.visibility = View.GONE
                isVisible = true
            }
            2 -> {
                isVisible = false
                //审核通过
                layout_auth.visibility = View.GONE
                layout_list.visibility = View.VISIBLE
                authSuccess = true
                presenter.requestList()
            }
        }
    }

    override fun onListServerError() {
        if (adapter.pageBean?.isRefresh!!) {
            recyclerView.setRefreshing(false)
        } else {
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR)
        }
    }

    override fun getAdapter(): ReceiveListAdapter {
        return adapter
    }


    override fun onWaitReceiveListSuccess(data: List<OrderM>?) {
        if (adapter.pageBean?.isRefresh!!) {
            recyclerView.setRefreshing(false)
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
            adapter.replaceAll(data)
            if (!CollectionUtils.isNullOrEmpty(data)) {
                recyclerView.visibility = View.VISIBLE
//                        emptyview.setVisibility(View.GONE)
            } else {
                recyclerView.visibility = View.GONE
//                        emptyview.setVisibility(View.VISIBLE)
            }
        } else {
            if (!CollectionUtils.isNullOrEmpty(data)) {
                recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
                adapter.addAll(data)
            } else {
                recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END)
//                        ToastUtil.showBottomtoast(activity, "没有更多数据啦")
            }
        }
    }

    override fun onRefresh() {
        adapter.pageBean?.isRefresh = true
        recyclerView.setRefreshing(true)
        presenter.requestList()
    }

    override fun onLoadMore(loadMoreView: View?) {
        recyclerView.loadMoreFooterView.visibility = View.VISIBLE
        adapter.pageBean?.isRefresh = false
        recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING)
        presenter.requestList()
    }


    //isWaitAuth 如果是待审核 显示对应的布局展示
    fun setVisible(isWaitAuth: Boolean) {
        if (isWaitAuth) {
            text_content.visibility = View.INVISIBLE
            text_title.visibility = View.INVISIBLE
            button_pay.visibility = View.INVISIBLE
            text_wait_tip.visibility = View.VISIBLE
        } else {
            text_content.visibility = View.VISIBLE
            text_title.visibility = View.VISIBLE
            button_pay.visibility = View.VISIBLE
            text_wait_tip.visibility = View.INVISIBLE
        }
    }

}