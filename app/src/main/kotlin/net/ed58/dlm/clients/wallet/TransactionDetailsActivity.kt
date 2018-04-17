package net.ed58.dlm.clients.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener
import com.aspsine.irecyclerview.widget.LoadMoreFooterView
import com.wise.common.commonutils.CollectionUtils
import com.wise.common.commonutils.LogUtil
import kotlinx.android.synthetic.main.activity_transaction_details.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.TransactionDetailsAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.AccountLogM
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by XiongHongJie on 2017/11/27.
 */

class TransactionDetailsActivity :BaseCoreActivity(),View.OnClickListener{


    private var adapter: TransactionDetailsAdapter? = null

    private var accountId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        image_back.setOnClickListener(this)

        accountId = intent.getStringExtra("accountId")




        adapter = TransactionDetailsAdapter(this)
        adapter?.setOnItemClickListener(object :OnItemClickListener<AccountLogM.AccountLogMList>{
            override fun onItemClick(parent: ViewGroup?, view: View?, t: AccountLogM.AccountLogMList?, position: Int) {

            }

            override fun onItemLongClick(parent: ViewGroup?, view: View?, t: AccountLogM.AccountLogMList?, position: Int): Boolean {
                return false
            }

        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.loadMoreFooterView.visibility = View.GONE
        recyclerView.adapter = adapter

        request()

        recyclerView.setOnRefreshListener {
            adapter!!.pageBean.isRefresh = true
            request()
        }
        recyclerView.setOnLoadMoreListener {
            recyclerView.loadMoreFooterView.visibility = View.VISIBLE
            adapter?.pageBean?.isRefresh = false
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING)
            request()
        }

    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.image_back -> onBackPressed()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_transaction_details
    }

    fun request(){
        val start: Int?
        Log.d("tag",adapter!!.pageBean.isRefresh.toString())

        if (adapter!!.pageBean.isRefresh){
            start = 0
        }else{
            start = adapter!!.size
        }

        Log.d("tag", adapter!!.size.toString())

        LogUtil.d(accountId)
        HttpMethods.getInstance().getAccountLogs(object :RxSubscriber<AccountLogM>(this){
            override fun _onNext(t: AccountLogM) {
                val list = t.list

                if (adapter!!.pageBean.isRefresh){
                    recyclerView.setRefreshing(false)
                    recyclerView.loadMoreFooterView.visibility = View.GONE
                    recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
                    adapter!!.replaceAll(list)
                }else{
                    if (!CollectionUtils.isNullOrEmpty(list)){
                        recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
                        adapter!!.addAll(list)
                    }else{
                        recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END)
                    }
                }


            }

            override fun _onError(code: Int, message: String?) {
                if (adapter?.pageBean?.isRefresh!!) {
                    recyclerView.setRefreshing(false)
                } else {
                    recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR)
                }
            }

        },accountId,start)
    }
    companion object {
        fun startActivity(context: Activity, accountId:String){
            val intent = Intent(context, TransactionDetailsActivity::class.java)
            intent.putExtra("accountId",accountId)
            context.startActivity(intent)
        }
    }
}