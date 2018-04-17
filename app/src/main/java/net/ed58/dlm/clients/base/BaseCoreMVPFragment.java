/**
 * Project Name:culiulib File Name:BaseFragment.java Package Name:com.culiu.core.fragment Date:2014-10-23下午5:19:31 Copyright (c)
 * 2014, adison All Rights Reserved.
 */

package net.ed58.dlm.clients.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;


/**
 * @describe Fragment基类(MVP).
 * @author adison
 * @date: 2014-10-23 下午5:19:31
 */
public abstract class BaseCoreMVPFragment<T extends BasePresenter<U>, U extends BaseUI> extends BaseCoreFragment {

    private T mPresenter;

    protected abstract T createPresenter();

    protected abstract U getUi();

    public T getPresenter() {
        return mPresenter;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = createPresenter();
        mPresenter.onFragmentAttach((BaseCoreActivity)activity, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 当fragment创建完成时调用该方法(即是当Activity对象完成自己的onCreate方法时调用。)，可以在此处进行联网或者其他逻辑处理.
     * @see Fragment#onActivityCreated(Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.onUiReady(getUi(), (BaseCoreActivity)getActivity());
    }

    /**
     * Fragment对象清理view资源时调用，也就是移除fragment中的视图.可以在此进行反注册操作
     * @see Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onUiDestroy(getUi());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //DebugLog.v("Front[BaseCoreMVPFragment]", "getArguments()-->" + getArguments() + "; savedInstanceState-->" + savedInstanceState);
//        mPresenter.onCreate((null != getArguments() ? getArguments() : savedInstanceState));
        mPresenter.onCreate(genBundle(savedInstanceState));
    }

    /**
     * 合成最合适的Bundle：将savedInstanceState 和 getArguments() 合成一个Bundle
     *
     * @author yedr
     * @param savedInstanceState
     * @return
     */
    private Bundle genBundle(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            if (null != getArguments()) {
                savedInstanceState.putAll(getArguments());
            }
            return savedInstanceState;
        } else {
            return getArguments();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
        showStatus("onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
        showStatus("onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
        showStatus("onPause");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mPresenter.onHiddenChanged(hidden);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if(fragments != null){
            for(Fragment f : fragments){
                if(f != null){
                    f.onHiddenChanged(hidden);
                }
            }
        }
    }

}
