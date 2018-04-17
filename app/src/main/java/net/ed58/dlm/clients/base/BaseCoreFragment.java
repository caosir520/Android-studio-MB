/**
 * Project Name:culiulib File Name:BaseCoeFragment.java Package Name:com.culiu.core.fragment Date:2014-11-14下午12:02:53 Copyright (c)
 * 2014, adison All Rights Reserved.
 */

package net.ed58.dlm.clients.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wise.common.baserx.RxManager;
import com.wise.common.commonutils.LogUtil;
import com.wise.common.commonutils.ViewFinder;
import com.wise.common.commonutils.ViewUtils;

import butterknife.ButterKnife;


/**
 * @describe Fragment基类..
 * @author adison
 * @date: 2014-11-14 下午12:02:53
 */
public abstract class BaseCoreFragment extends Fragment {

    protected ViewFinder mViewFinder;

    protected Activity mActivity;

    protected Bundle mDataIn;

    /**
         * Fragment是否已经Detach了 为什么初始化值为true？因为boolean类型的值默认情况下是false;
     **/
    protected boolean mIsFragmentDetached = true;
    
    private boolean mIsFragmentHidden = false;

    protected RxManager mRxManager;

    /**
     * createView:fragment在其中创建自己的layout(界面)。. <br/>
     * @author adison
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /** Fragment的根View **/
    protected View rootView = null;

    /**
     * 当fragment被加入到activity时调用,在这个方法中可以获得所在的activity
     * @see Fragment#onAttach(Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mIsFragmentDetached = false;
    }

    /**
     * 当activity要得到fragment的layout时，调用此方法，fragment在其中创建自己的layout(界面)。
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = createView(inflater, container, savedInstanceState);
            mRxManager=new RxManager();
            ButterKnife.bind(this, rootView);
            executeOnceAfterCreateView();
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup)rootView.getParent();
        if(parent != null) {
            parent.removeView(rootView);
        }
        mViewFinder = new ViewFinder(rootView);
        return rootView;
    }
    /**
     *
     * executeOnceAfterCreateView:在CreateView之后执行一次. <br/>
     *
     * 这个方法只会执行一次，除非rootView变成null
     * @author wangheng
     */
    protected void executeOnceAfterCreateView() {

    }

    /**
     * 当fragment创建layout(界面)完成时调用该方法，可以在此初始化控件及注册对应的监听事件.
     * @see Fragment#onViewCreated(View, Bundle)
     */
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
    }

    /**
     * Fragment对象清理view资源时调用，也就是移除fragment中的视图.可以在此进行反注册操作
     * @see Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(rootView != null && rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * Fragment对象完成对象清理View资源时调用.
     * @see Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mRxManager != null) {
            mRxManager.clear();
        }

    }

    /**
     * Fragment对象没有与Activity对象关联时调用.
     * @see Fragment#onDetach()
     */
    @Override
    public void onDetach() {
        mIsFragmentDetached = true;
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        showStatus("onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        showStatus("onResume");
    }


    // ===========================================================

    public void showStatus(String status) {
        LogUtil.d(String.format("%s %s", this.getClass().getName(), status));
    }

    /**
     * 展示View
     * @param view
     * @return
     */
    protected BaseCoreFragment show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    /**
     * 隐藏View
     * @param view
     * @return
     */
    protected BaseCoreFragment hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * isFragmentDetached:Fragment是否已经Detach了;. <br/>
     * 如果Fragment还没有Attach到Activity，则返回true(默认值).<br/>
     * @author wangheng
     * @return
     */
    public boolean isFragmentDetached() {

        return mIsFragmentDetached;
    }

    /**
     *
     * getListView:得到界面中的ListView. <br/>
     *
     * @author wangheng
     * @return
     */
    public ListView getListView() {
        return null;
    }

    /**
     * @MethodName: isDead
     * @Description: 判断Fragment或依附的Activity是否正在被销毁或销毁中
     * @param  @return
     * @return boolean    返回类型
     * @author wangjing
     */
    public boolean isDead() {
        if(isFragmentDetached() || getActivity() == null || getActivity().isFinishing()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 监控父类Fragment的可见性.
     * @see Fragment#onHiddenChanged(boolean)
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mIsFragmentHidden = hidden;
    }
    /**
     * @MethodName: isPageVisible
     * @Description: 判断Fragment是否可见
     * @param  
     * @return boolean    返回类型
     * @author zhangyang
     */
    public boolean isPageVisible(){
        if(isHidden() || ! getUserVisibleHint() || mIsFragmentHidden)
            return false;
        return true;
    }


}
