/**
 * Project Name:culiulib File Name:BasePresenter.java Package Name:com.culiu.core.fragment.presenter Date:2014-10-24上午2:32:54
 * Copyright (c) 2014, adison All Rights Reserved.
 */

package net.ed58.dlm.clients.base;

import android.os.Bundle;

import net.ed58.dlm.clients.Insured.InsuredActivity;


/**
 * @describe Presenter基类.
 *           <P>
 *           MVP中的P
 *           </P>
 */
public abstract class BasePresenter<U extends BaseUI> implements BasePresenterInterface {

    private U mUi;

    private BaseCoreActivity mActivity;

    private BaseCoreFragment mFragment;
    /**
     * onFragmentAttach:在Fragment依附到Activity时调用. <br/>
     * @author adison
     * @param activity
     */
    public void onFragmentAttach(BaseCoreActivity activity, BaseCoreFragment fragment) {
        mActivity = activity;
        mFragment = fragment;
    }


    /**
     * onUiReady:在UI创建完成后调用 {@link #BaseFragment.onActivityCreated() },{@link #BaseNoFragmentActivity.onCreate() }. <br/>
     * @author adison
     * @param ui 具体UI实现
     * @param context
     */
    public void onUiReady(U ui, BaseCoreActivity activity) {
        mUi = ui;
        mActivity = activity;
    }

    /**
     * onUiDestroy:在UI销毁时调用 {@link #BaseFragment.onDestroyView() },{@link #BaseNoFragmentActivity.onDestroy() }. <br/>
     * @author adison
     * @param ui 具体UI实现
     */
    public final void onUiDestroy(U ui) {
        onUiUnready(ui);
        mUi = null;
    }

    /**
     * onUiUnready:Presenter实现类复写这个方法. <br/>
     * <P>
     * 当Fragment或者Activity即将被销毁时调用，可用于取消监听事件、取消注册事件
     * </p>
     * @author adison
     * @param ui
     */
    public void onUiUnready(U ui) {
    }

    public U getUi() {
        return mUi;
    }

    public BaseCoreActivity getActivity() {
        return mActivity;
    }

    public BaseCoreFragment getFragment() {
        return mFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        // 默认空实现
    }

    @Override
    public void onStart() {
        // 默认空实现
    }

    @Override
    public void onResume() {
        // 默认空实现
    }

    @Override
    public void onPause() {
        // 默认空实现
    }

    @Override
    public void onDestroy() {
        // 默认空实现
    }

    @Override
    public void onStop() {
        // 默认空实现
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // 默认空实现
    }

}
