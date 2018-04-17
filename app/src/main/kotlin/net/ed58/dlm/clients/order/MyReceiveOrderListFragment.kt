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
import net.ed58.dlm.clients.adapter.ReceiveRecListAdapter
import net.ed58.dlm.clients.base.BaseCoreFragment
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.entity.PageResultList
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.sp.MyConfiguration

/**\
 * 接单列表的Fragment
 * Created by Administrator on 2017/11/13/013.
 */

class MyReceiveOrderListFragment : BaseCoreFragment(), OnRefreshListener, OnLoadMoreListener {

    private var mView: View? = null
    private var adapter: ReceiveRecListAdapter? = null


    private var isPrepared: Boolean = false
    private var mHasLoadedOnce: Boolean = false
    private var index: Int = 0
    private var orderStatus: Int? = -1
    private var isRegistRx = false//防止重复注册

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_my_send_order_list, container, false)
            isPrepared = true// 显示进度条
        }
        return mView!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        index = arguments.getInt("index")
        when (index) {
            0 -> {
                orderStatus = 400
            }
            1 -> {
                orderStatus = 500
            }
            2 -> {
                orderStatus = -10000
                if (isRegistRx) {
                    return
                }
                isRegistRx = true
                LogUtil.d("registRx")
                isRegistRx = true
                mRxManager.on<Int>(RxKey.EVENT_ALL_RECEIVE_LIST_ITEM) {
                    adapter?.pageBean?.isRefresh = true
                    request()
                }
            }
        }


        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setOnRefreshListener(this)
        recyclerView.setOnLoadMoreListener(this)
        recyclerView.loadMoreFooterView.visibility = View.GONE
        adapter = ReceiveRecListAdapter(activity)
        adapter?.index = index
        recyclerView.adapter = adapter


        //为了切换tab的时候每次都加载这个tab的数据，避免预先加载
        if (!mHasLoadedOnce && userVisibleHint) {
            LogUtil.d("${index}")
            lazyLoad()
        }

    }


    private fun lazyLoad() {
        //加载数据操作
        adapter?.pageBean?.isRefresh = true

        request()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isPrepared) {
            LogUtil.d("${index}")
            mHasLoadedOnce = true
            lazyLoad()
        }
    }


    private fun request() {
        val size: Int?
        if (adapter?.pageBean?.isRefresh!!) {
            size = 0
        } else {
            size = adapter?.size
        }
        HttpMethods.getInstance().orderTakedList(object : RxSubscriber<PageResultList<OrderM>>(activity) {
            override fun _onError(code: Int, message: String?) {
                if (adapter?.pageBean?.isRefresh!!) {
                    recyclerView.setRefreshing(false)
                } else {
                    recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR)
                }
            }

            override fun _onNext(t: PageResultList<OrderM>?) {
                val data = t?.list
                if (adapter?.pageBean?.isRefresh!!) {
                    recyclerView.setRefreshing(false)
                    recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
                    adapter?.replaceAll(data)
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
                        adapter?.addAll(data)
                    } else {
                        recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END)
//                        ToastUtil.showBottomtoast(activity, "没有更多数据啦")
                    }
                }
            }


        }, "" + orderStatus, "" + size, "10", MyConfiguration.getInstance().getMyLongtitude(activity), MyConfiguration.getInstance().getMyLatitude(activity))
    }


    override fun onLoadMore(loadMoreView: View?) {
        recyclerView.loadMoreFooterView.visibility = View.VISIBLE
        adapter?.pageBean?.isRefresh = false
        recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING)
        request()
    }

    override fun onRefresh() {
        adapter?.pageBean?.isRefresh = true
        recyclerView.setRefreshing(true)
        request()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.REQUEST_FIRST_CODE,
                Constant.REQUEST_SECOND_CODE -> {
                    adapter?.remove(adapter?.get(adapter?.position!!))
                }

            }
        }
    }
}
