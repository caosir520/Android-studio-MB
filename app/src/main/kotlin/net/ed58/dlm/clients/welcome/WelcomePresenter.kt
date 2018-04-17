package net.ed58.dlm.clients.welcome

import android.app.Activity
import com.wise.common.baserx.RxBus
import com.wise.common.baserx.RxManager
import com.wise.common.commonutils.LogUtil
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.sp.MyConfiguration
import rx.functions.Action1
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class WelcomePresenter(mRxManager: RxManager) : BasePresenter<WelcomePresenter.IWelcomeView>() {

    /** 业务逻辑模块  */
    private var mModel: IWelcomeModel? = null

    /** 界面等待完成状态变量  */
    private var mIsViewWaitCompleted = false

    /** 异步初始化完成状态变量  */
    private var mIsAsyncExecuteCompleted = false

    /** 界面是否pause掉了,默认情况下是false，这个变量在Activity的生命周期方法onResume()、onPause()方法回调的时候会进行修改  */
    private var mIsViewPaused = false


    init {
        mModel = WelcomeModel()

        mRxManager.on(RxKey.EVENT_WELCOME_INIT_COMPLETED, Action1<WelcomeEvent> { welcomeEvent -> onEvent(welcomeEvent) })
    }

    override fun onUiReady(ui: IWelcomeView, activity: BaseCoreActivity) {
        super.onUiReady(ui, activity)
        // 执行界面等待
        viewWaiting(VIEW_WAIT_TIME)
        mModel!!.syncInit(activity)

        // 异步初始化: 另起一个线程执行
        //        executeAsyncInit();
        // 延时初始化：延时后，在UI主线程执行
        delayInit()
    }

    /**
     * onEventMainThread:处理Welcome中的事件. <br></br>
     * @author wangheng
     * @param event
     */
    fun onEvent(event: WelcomeEvent?) {
        if (event != null) {
            when (event) {
                WelcomeEvent.EVENT_VIEW_WAIT_COMPLETED -> {
                    mIsViewWaitCompleted = true
                    initComplete()
                }
                WelcomeEvent.EVENT_ASYNC_INIT_COMPLETED -> {
                    mIsAsyncExecuteCompleted = true
                    initComplete()
                }
            }
        }
    }

    /**
     * 初始化完成，执行下一步操作 有可能进入引导界面或者程序主界面
     */
    private fun initComplete() {
        /** 如果异步初始化、界面等待都完成了  */
        if (mIsAsyncExecuteCompleted && mIsViewWaitCompleted && !mIsViewPaused) {
            if (ui == null) {
                return
            }
            if (MyConfiguration.getInstance().isFirstUse(activity)) { // 如果是第一次使用
                //                getUi().enterGuide();
                ui.enterMain()
            } else {
                ui.enterMain()
            }
        }


    }

    /**
     * 界面最少等待一段时间才可以进入到主界面
     * @param viewWaitTime 界面等待时间
     */
    private fun viewWaiting(viewWaitTime: Int) {
        val service = Executors.newScheduledThreadPool(1)

        val command = Runnable {
            // 界面等待结束
            RxBus.getInstance().post(RxKey.EVENT_WELCOME_INIT_COMPLETED, WelcomeEvent.EVENT_VIEW_WAIT_COMPLETED)
        }

        service.schedule(command, viewWaitTime.toLong(), TimeUnit.MILLISECONDS)
        service.shutdown()
    }

    /**
     * 执行异步初始化：另起一个线程执行
     */
    /*    private void executeAsyncInit() {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

         Runnable command = new Runnable() {

             @Override
             public void run() {
                 try {
                     //mModel.asyncInit(getActivity());
                 } catch(Exception e) {
                     DebugLog.e(e.getMessage());
                 } finally {
                    EventBus.getDefault().post(WelcomeEvent.EVENT_ASYNC_INIT_COMPLETED);
                 }
             }
         };

         service.schedule(command, 500, TimeUnit.MILLISECONDS);
         service.shutdown();
    }*/

    /**
     * 执行延时初始化：依然在UI主进程中执行操作
     */
    private fun delayInit() {
        try {
            mModel!!.asyncInit(activity)
        } catch (e: Exception) {
            LogUtil.e(e.message)
        } finally {
            RxBus.getInstance().post(RxKey.EVENT_WELCOME_INIT_COMPLETED, WelcomeEvent.EVENT_ASYNC_INIT_COMPLETED)
        }

        //        final Handler handler = new Handler();
        //        final Runnable runnable = new Runnable() {
        //
        //            @Override
        //            public void run() {
        //                try {
        //                    mModel.asyncInit(getActivity());
        //                } catch(Exception e) {
        //                    LogUtil.e(e.getMessage());
        //                } finally {
        //                   EventBus.getDefault().post(WelcomeEvent.EVENT_ASYNC_INIT_COMPLETED);
        //                }
        //            }
        //        };
        //        handler.postDelayed(runnable, DELAY_TIME);
    }


    /**
     * 当界面onResume的时候被执行
     */
    override fun onResume() {
        mIsViewPaused = false
        initComplete()
    }


    /**
     * 当界面onPause的时候被执行
     */
    override fun onPause() {
        mIsViewPaused = true
    }


    /**
     * 欢迎界面的业务
     * @author wangheng
     */
    interface IWelcomeModel {

        fun syncInit(activity: Activity)

        /**
         * asyncInit:异步初始化. <br></br>
         * @author wangheng
         */
        fun asyncInit(activity: Activity)
    }

    /**
     * 欢迎界面的View层都要实现这个协议
     * @author wangheng
     */
    interface IWelcomeView : BaseUI {

        /**
         * 进入到主界面
         */
        fun enterMain()

        /**
         * 进入到引导界面
         */
        fun enterGuide()
    }

    companion object {

        /** 界面最少等待时间  */
        private val VIEW_WAIT_TIME = 2000

        /** 延时初始化的时间  */
        private val DELAY_TIME: Long = 500 // Unit: ms
    }
}
