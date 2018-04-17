package net.ed58.dlm.clients.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.util.LocationUtils

class MainActivity : BaseCoreMVPActivity<MainPresent, MainPresent.IMainView>(), MainPresent.IMainView, RadioGroup.OnCheckedChangeListener {

    private var mainPresent: MainPresent? = null
    private var exitTime: Long = 0
    private var tabPosition:Int = -1//0和1是已发订单的tab位置，2是到已接订单

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rg_homeTab!!.setOnCheckedChangeListener(this)
        mainTabHome.isChecked = true

        //注册支付或下单后的监听，改变tab 后几个页面同时也注册了
        mRxManager.on<Int>(RxKey.EVENT_PAY_GO_MAIN_ORDER_TAB) { sendOrderTabPosition ->
            this.tabPosition = sendOrderTabPosition
            mainTabOrder.isChecked = true
        }
        //查看我的接单列表
        mRxManager.on<Int>(RxKey.EVENT_GO_MY_RECEIVE_ORDER_LIST){sendOrderTabPosition->
            this.tabPosition = sendOrderTabPosition
            mainTabOrder.isChecked = true
        }

        LocationUtils.location()
    }

    override fun createPresenter(): MainPresent {
        if (mainPresent == null) {
            mainPresent = MainPresent(this)
        }
        return mainPresent!!
    }

    override fun getUi(): MainPresent.IMainView {
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }


    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.mainTabHome -> mainPresent?.showTabHome()
            R.id.mainTabOrder -> {
                mainPresent?.showTabOrder(tabPosition)
                tabPosition = -1//操作过后置位
            }
            R.id.mainTabMe -> mainPresent?.showTabMe()
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(applicationContext, "再按一次就要离开我了！", Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            System.exit(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.getCurrentFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    fun getFragmentContainerId(): Int {
        return R.id.container
    }


}