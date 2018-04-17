package net.ed58.dlm.clients.welcome

import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.wise.common.baseapp.AppManager
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.login.LoginActivity
import net.ed58.dlm.clients.main.MainActivity
import net.ed58.dlm.clients.sp.SPKey
import net.ed58.dlm.clients.sp.SettingUtils

class WelcomeActivity : BaseCoreMVPActivity<WelcomePresenter, WelcomePresenter.IWelcomeView>(), WelcomePresenter.IWelcomeView {

    override fun enterMain() {
        if (!SettingUtils.getSharedPreferences(this@WelcomeActivity, SPKey.KEY_FINISH_FILL_INFO, false)) {
            val intent = Intent()
            intent.setClass(this@WelcomeActivity, LoginActivity::class.java)

            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            AppManager.getAppManager().finishActivity()
        } else {
            val intent = Intent()
            intent.setClass(this@WelcomeActivity, MainActivity::class.java)

            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            AppManager.getAppManager().finishActivity()
        }
    }

    override fun enterGuide() {

    }

    override fun createPresenter(): WelcomePresenter {
        return WelcomePresenter(mRxManager)
    }

    override fun getUi(): WelcomePresenter.IWelcomeView {
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_welcome
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        if ((intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) !== 0) {
            finish()
            return
        }
    }
}
