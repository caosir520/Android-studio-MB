package net.ed58.dlm.clients

import android.content.Context
import com.wise.common.baseapp.BaseApplication

/**
 * Created by sunpeng on 15/11/13.
 */
class MyApplication : BaseApplication() {



    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: Context

        fun getContext(): Context {
            return instance.applicationContext
        }
    }
}
