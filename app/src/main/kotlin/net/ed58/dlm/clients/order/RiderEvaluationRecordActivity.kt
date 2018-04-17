package net.ed58.dlm.clients.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aspsine.irecyclerview.widget.LoadMoreFooterView
import com.wise.common.commonutils.CollectionUtils
import com.wise.common.commonutils.ImageLoaderUtils
import kotlinx.android.synthetic.main.activity_rider_evaluation_record.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.RiderEvaluationRecordAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.ReviewM
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

class RiderEvaluationRecordActivity : BaseCoreActivity(),View.OnClickListener {

    private var adapter: RiderEvaluationRecordAdapter? = null

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        image_back.setOnClickListener(this)

        userId = intent.getStringExtra("userId")
        adapter = RiderEvaluationRecordAdapter(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.loadMoreFooterView.visibility = View.GONE
        recyclerView.adapter = adapter

        request()
        recyclerView.setOnRefreshListener {
            adapter!!.pageBean.isRefresh = true
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
            request()
        }

        recyclerView.setOnLoadMoreListener {
            adapter!!.pageBean.isRefresh = false
            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING)
            request()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.image_back -> onBackPressed()
        }
    }

    private fun request(){
        val start: Int?

        if(adapter?.pageBean!!.isRefresh){
            start = 0
        }else{
            start = adapter!!.size
        }

        HttpMethods.getInstance().searchUserReviewList (object: RxSubscriber<ReviewM>(this){
                override fun _onNext(t: ReviewM?) {



                    val list = t?.reviewList

                    if (adapter!!.pageBean.isRefresh){
                        ImageLoaderUtils.displayRound(mContext,head_image,t?.head)
                        rider_name.text = t?.nickName
                        rating.text = t?.rating + "分"
                        receive_order_num.text = t?.times.toString() + "单"
                        recyclerView.setRefreshing(false)
                        recyclerView.loadMoreFooterView.visibility = View.GONE
                        adapter!!.replaceAll(list)
                    }else{
                        if (!CollectionUtils.isNullOrEmpty(list)){
                            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.GONE)
                            adapter!!.addAll(list)
                        }else{
                            recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END)
                            recyclerView.loadMoreFooterView.visibility = View.VISIBLE
                        }
                    }
                }

            override fun _onError(code: Int, message: String?) {
                if (adapter!!.pageBean.isRefresh){
                    recyclerView.setRefreshing(false)
                }else{
                    recyclerView.setLoadMoreStatus(LoadMoreFooterView.Status.ERROR)
                }
            }

        },userId,1,start)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_rider_evaluation_record
    }

    companion object {
        fun startActivity(context: Context, userId:String){
            val intent = Intent(context, RiderEvaluationRecordActivity::class.java)
            intent.putExtra("userId",userId)
            context.startActivity(intent)
        }
    }
}
