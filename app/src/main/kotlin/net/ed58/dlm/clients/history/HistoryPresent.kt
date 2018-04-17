package net.ed58.dlm.clients.history

import com.wise.common.commonutils.LogUtil
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.Address
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 *创建人: caosir
 *创建时间：2017/10/30
 *修改人：
 *修改时间：
 *类说明：
 */
 class HistoryPresent :BasePresenter<HistoryPresent.HostoryView>(){
    //获取
    fun getHostroyList(start:String ,limit:String){
        LogUtil.d("获取历史地址"+start)
        val httpMethods  =HttpMethods.getInstance()
        val subscribe =object : RxSubscriber<Address>(activity){
            override fun _onError(code: Int, message: String?) {
            }

            override fun _onNext(t: Address?) {


                LogUtil.d("成功返回信息"+t?.total+t?.list?.size)
                ui.showHostoryList(t?.list)

            }

        }
        httpMethods.getHostoryAddressList(subscribe,"0","10")
     /*   val address:MutableList<Address.ListEntity>
        for (i in 1..100){

            val listEntity = Address.ListEntity()
            listEntity.name = "曹振荣"
            listEntity.tel="15870617281"+i

        }
        val list = listOf<Int>(1,2,34,5)*/

    }
    //定义UI接口
    interface HostoryView:BaseUI{
        fun showHostoryList(address:List<Address.ListEntity>?)


    }
}