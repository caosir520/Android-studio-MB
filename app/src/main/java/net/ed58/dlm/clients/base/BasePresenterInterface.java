/**
 * Project Name:culiulib File Name:BasePresenterInterface.java Package Name:com.culiu.core.presenter Date:2014年11月18日上午11:00:46
 * Copyright (c) 2014, adison All Rights Reserved.
 */

package net.ed58.dlm.clients.base;

import android.os.Bundle;

/**
 * @describe PresenterInterface .主要为了应对Activity或者 Fragment生命周期对应处理问题
 * @author adison
 * @date: 2014年11月18日 上午11:00:46
 */
public interface BasePresenterInterface {

    /**
     * onCreate:对应生命周期方法onCreate(). <br/>
     * @author adison
     */
    void onCreate(Bundle bundle);

    /**
     * onStart:对应生命周期方法onStart(). <br/>
     * @author adison
     */
    void onStart();

    /**
     * onResume:对应生命周期方法onResume(). <br/>
     * @author adison
     */
    void onResume();

    /**
     * onPause:对应生命周期方法onPause(). <br/>
     * @author adison
     */
    void onPause();

    /**
     * onDestroy:对应生命周期方法onDestroy(). <br/>
     * @author adison
     */
    void onDestroy();

    /**
     * onStop:对应生命周期方法onStop(). <br/>
     * @author adison
     */
    void onStop();

    /**
     * onHiddenChanged:对应Fragment生命周期方法onHiddenChanged(). <br/>
     * @author adison
     * @param hidden
     */
    void onHiddenChanged(boolean hidden);
}
