package net.ed58.dlm.clients.main

import android.os.Bundle
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI

/**
 * Created by sunpeng on 17/2/10.
 */

class MainOrderPresent : BasePresenter<MainOrderPresent.IMainOrder>() {



    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
    }


    interface IMainOrder : BaseUI {

    }

}