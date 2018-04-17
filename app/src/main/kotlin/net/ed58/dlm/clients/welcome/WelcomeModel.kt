package net.ed58.dlm.clients.welcome

import android.app.Activity

class WelcomeModel : WelcomePresenter.IWelcomeModel {


    /**
     * syncInit:执行同步初始化工作. <br></br>
     * 建议将必须要求UI主进程同步执行的操作，才放置到此处；<br></br>
     * 否则，放置到[.asyncInit]
     *
     */
    override fun syncInit(activity: Activity) {}

    /**
     * 异步初始化操作： 执行Welcome模块中耗时的初始化任务
     */
    override fun asyncInit(activity: Activity) {

    }


}
