package net.ed58.dlm.clients.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener
import com.wise.common.baseapp.AppManager
import com.wise.common.commonutils.LogUtil
import kotlinx.android.synthetic.main.activity_history.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.HistoryAdapter
import net.ed58.dlm.clients.address.MapSelectAddressActivity
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.Address
import net.ed58.dlm.clients.entity.AddressInfoBean
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.history.HistoryPresent.HostoryView
import net.ed58.dlm.clients.order.FillAddressInfoActivity
import java.util.*

/**
 * 创建人: caosir
 * 创建时间：2017/10/3
 * 修改人：
 * 修改时间：
 * 类说明：历史信息的Activity
 */

class HistoryActivity : BaseCoreMVPActivity<HistoryPresent, HostoryView>(), HostoryView, View.OnClickListener {



    var historyAdapter: HistoryAdapter? = null
    //历史List的堆，用于实现单选模式
    private val historyItemSelect = Stack<Int>()

    private var isFillAddressPage= false//是否是填写地址页面过来的
    private var type: Int = 0

    override fun createPresenter(): HistoryPresent {
        return HistoryPresent()
    }

    override fun getUi(): HostoryView {
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = intent.getIntExtra("type", -1)
        isFillAddressPage = intent.getBooleanExtra("isFillAddressPage", false)
        presenter.getHostroyList("0", "50")

        im_history_back.setOnClickListener(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_history
    }


    override fun onClick(view: View?) {
        onBackPressed()
    }

    override fun showHostoryList(address: List<Address.ListEntity>?) {

//        if (address!!.size == 0) {
//            tv_rv_null!!.visibility = View.VISIBLE
//        } else {
//            tv_rv_null!!.visibility = View.GONE
//        }

        historyAdapter = HistoryAdapter(this, R.layout.history_address_item, address)
        historyAdapter!!.setOnItemClickListener(object :OnItemClickListener<Address.ListEntity> {

            override fun onItemClick(parent: ViewGroup, view: View, t: Address.ListEntity?, position: Int) {
                LogUtil.d("viewID" + view.isSelected)
                val view1 = view as ViewGroup
                //重堆中获取上次的点击位子
                if (!historyItemSelect.empty()) {
                    //当堆不为空的时候
                    //修改上次数据标识
                    val i = historyItemSelect.pop()
                    historyAdapter!!.get(i)!!.isIsDefault =!historyAdapter!!.get(i)!!.isIsDefault
                    //修改上次item的图标
                    historyAdapter!!.notifyItemChanged(i)
                }

                //修改本次图标
//                val image = view1!!.findViewById(R.id.im_select) as ImageView
                val image = view1.findViewById<ImageView>(R.id.im_select)
                historyAdapter!!.get(position)!!.isIsDefault = !historyAdapter!!.get(position)!!.isIsDefault
                image.isSelected = historyAdapter!!.get(position)!!.isIsDefault
                //这次数据压栈
                historyItemSelect.push(position)

                //RXbus 通知
                if (!historyItemSelect.empty()) {
                    //当堆不为空的时候
                    //修改上次数据标识
                    val i = historyItemSelect.pop()
                    LogUtil.d("dasdas", historyAdapter!!.get(i)!!.address)
                    val listEntity = historyAdapter!!.get(i)
                    val addressInfoBean = AddressInfoBean()
                    addressInfoBean.adCode = listEntity!!.adCode
                    addressInfoBean.cityCode = listEntity.cityCode
                    addressInfoBean.address = listEntity.address
                    addressInfoBean.name = listEntity.name
                    addressInfoBean.phone = listEntity.tel
                    addressInfoBean.latitude = listEntity.lat
                    addressInfoBean.longtitude = listEntity.lng
                    addressInfoBean.detailAddress = listEntity.floorHouseNum
                    addressInfoBean.isCompleted = true

                    if ((!isFillAddressPage) && type == FillAddressInfoActivity.RECEIVE_TYPE) {
                        FillAddressInfoActivity.startActivity(this@HistoryActivity, type, addressInfoBean)
                    } else {
                        mRxManager.post(RxKey.EVENT_SELECT_COMMON_ADDRESS, addressInfoBean)
                    }
                    AppManager.getAppManager().finishActivity(MapSelectAddressActivity::class.java)
                    AppManager.getAppManager().finishActivity()
                }
            }

            override fun onItemLongClick(parent: ViewGroup, view: View, t: Address.ListEntity?, position: Int): Boolean {
                return false
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = historyAdapter
    }

    companion object {


        fun startActivity(activity: Activity, type: Int, isFillAddressPage: Boolean) {
            val intent = Intent(activity, HistoryActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("isFillAddressPage", isFillAddressPage)
            activity.startActivity(intent)

        }
    }


}
