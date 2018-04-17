package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aspsine.irecyclerview.OnLoadMoreListener
import com.aspsine.irecyclerview.OnRefreshListener
import com.aspsine.irecyclerview.widget.LoadMoreFooterView
import com.wise.common.commonutils.CollectionUtils
import com.wise.common.commonutils.LogUtil
import kotlinx.android.synthetic.main.fragment_my_send_order_list.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.SendListAdapter
import net.ed58.dlm.clients.base.BaseCoreFragment
import net.ed58.dlm.clients.entity.OrderListBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by sunpeng on 17/11/7.
 * 已发订单列表
 */

class MySendOrderListFragment : BaseCoreFragment(), OnRefreshListener, OnLoadMoreListener {

    private var mView: View? = null
    private lateinit var adapter: SendListAdapter


    private var isPrepared: Boolean = false
    private var mHasLoadedOnce: Boolean = false
    private var index: Int = 0
    private var orderStatus: String = ""
    private var isRegistRx = false//防止重复注册

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_my_send_order_list, container, false)
            isPrepared = true
        }
        return mView!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        index = arguments.getInt("index")
        when (index) {
            0 -> orderStatus = "0,100"//待支付
            1 -> orderStatus = "300"//待接单
            2 -> orderStatus = "400,500,600,700,-500,-400,-1"//已接单
            3 -> {//更多
                orderStatus = "-10000"
            }
        }
        //注册对应页面的监听
        registRxListener(index)


        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setOnRefreshListener(this)
        recyclerView.setOnLoadMoreListener(this)
        recyclerView.loadMoreFooterView.visibility = View.GONE
        adapter = SendListAdapter(activity)
        adapter.index = index
        recyclerView.adapter = adapter


        //为了切换tab的时候每次都加载这个tab的数据，避免预先加载
        if (!mHasLoadedOnce && userVisibleHint)
            lazyLoad()
    }

    fun registRxListener(index: Int) {
        if (isRegistRx) {
            return
        }
        isRegistRx = true
        when (index) {
            0 -> {
                LogUtil.d("registRx--0")
                //针对去支付的单
                mRxManager.on<Boolean>(RxKey.EVENT_CANCEL_SEND_LIST_ITEM) {
                    adapter.remove(adapter.get(adapter.position))
                }
            }
            3 -> {
                LogUtil.d("registRx--3")
                //针对 全部列表里面的操作
                mRxManager.on<Int>(RxKey.EVENT_ALL_SEND_LIST_ITEM) { position ->
                    adapter.pageBean?.isRefresh = true
                    request()
                }
            }
        }

    }


    fun lazyLoad() {
        //加载数据操作
        adapter.pageBean?.isRefresh = true

        request()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isPrepared) {
            mHasLoadedOnce = true
            lazyLoad()
        }
    }


    private fun request() {
        LogUtil.d("hehehehhe")
        val size: Int?
        if (adapter.pageBean?.isRefresh!!) {
            size = 0
        } else {
            size = adapter.size
        }
        HttpMethods.getInstance().getSendOrderList(object : RxSubscriber<OrderListBean>(activity) {
            override fun _onNext(orderListBean: OrderListBean?) {
                val data = orderListBean?.list
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

            override fun _onError(code: Int, message: String?) {
                if (adapter.pageBean?.isRefresh!!) {
                    recyclerView.setRefreshing(false)
                } else {
                    recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR)
                }
            }

        }, orderStatus, size)
    }


    override fun onLoadMore(loadMoreView: View?) {
        recyclerView.loadMoreFooterView.visibility = View.VISIBLE
        adapter.pageBean?.isRefresh = false
        recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING)
        request()
    }

    override fun onRefresh() {
        adapter.pageBean?.isRefresh = true
        recyclerView.setRefreshing(true)
        request()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_FIRST_CODE) {
                //非全部列表操作的  取消订单
                adapter.remove(adapter.get(adapter.position))
            } else if (requestCode == Constant.REQUEST_SECOND_CODE) {
                //已结单列表 操作的  取消订单
                adapter.get(adapter.position)?.originatorReviewStatus = 1
                adapter.notifyDataSetChanged()
            }
        }
    }
}