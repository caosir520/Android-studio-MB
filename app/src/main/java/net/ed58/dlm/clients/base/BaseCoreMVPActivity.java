/**
 * Project Name:culiulib File Name:BaseMVPActivity.java Package Name:com.culiu.core.activity Date:2014-10-29下午3:41:03 Copyright (c)
 * 2014, adison All Rights Reserved.
 */

package net.ed58.dlm.clients.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.wise.common.commonutils.LogUtil;
import com.wise.common.commonutils.StarusUtil;


/**
 * @describe Activity基类.
 * 
 *           <Pre>
 *           当页面页面元素较多，响应事件较多，且逻辑比较复杂建议继承该类,且页面使用MVP模式
 * </pre>
 * @author adison
 * @date: 2014-10-29 下午3:41:03
 * @param <T> Presenter
 * @param <U> UI
 */
public abstract class BaseCoreMVPActivity<T extends BasePresenter<U>, U extends BaseUI> extends BaseCoreActivity {

    private T mPresenter;

    /**
     * createPresenter:创建Presenter. <br/>
     * @author adison
     * @return
     */
    protected abstract T createPresenter();

    /**
     * getUi:获取对应的UI. <br/>
     * <P>
     * 当前主要作用是为了限制操作及防止出错
     * <P>
     * @author adison
     * @return
     */
    protected abstract U getUi();

    public T getPresenter() {
        return mPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = createPresenter();
        mPresenter.onUiReady(getUi(), this);
        mPresenter.onCreate(getIntent().getExtras());
        LogUtil.d("onCreate---");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
        LogUtil.d("onStart---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onUiDestroy(getUi());
        mPresenter.onDestroy();
        ViewGroup
        LogUtil.d("onDestroy---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        LogUtil.d("onResume---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
        LogUtil.d("onPause---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
        LogUtil.d("onStop---");
    }
    
//    @Override
//    protected int getFragmentContainerId() {
//        return 0;
//    }

}
