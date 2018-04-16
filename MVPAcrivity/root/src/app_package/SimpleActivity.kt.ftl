package net.ed58.dlm.clients.me

import ${packageName}.${presentName};
import net.ed58.dlm.clients.base.BaseCoreMVPActivity

/**
 * Created by Administrator on 2017/11/27/027.
 */

class ${activityClass}: BaseCoreMVPActivity<${presentName}, ${presentName}.Ui>(),${presentName}.Ui{
    override fun createPresenter(): ${presentName} {
       return ${presentName}()
    }

    override fun getUi(): ${presentName}.Ui {
      return this
    }

    override fun getLayoutId(): Int {
       return R.layout.${layoutName}
    }

}
