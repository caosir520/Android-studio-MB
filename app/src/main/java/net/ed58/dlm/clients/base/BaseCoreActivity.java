package net.ed58.dlm.clients.base;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.wise.common.Global;
import com.wise.common.baseapp.AppManager;
import com.wise.common.baserx.RxManager;
import com.wise.common.commonutils.LogUtil;
import com.wise.common.commonutils.PackageUtils;
import com.wise.common.commonutils.StarusUtil;
import com.wise.common.commonwidget.LoadingDialog;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.util.LocationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;


/**
 * 基类
 */

/***************
 * 使用例子
 *********************/
public abstract class BaseCoreActivity extends AppCompatActivity {
    public Context mContext;
    public RxManager mRxManager;

    //输入法管理器
    private InputMethodManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("Create");
        AppManager.getAppManager().addActivity(this);
        mRxManager = new RxManager();
        //处理一体化：
        if (StarusUtil.StatusBarLightMode(this) != 0) {
            StarusUtil.setStatusBarColor(this, R.color.white); //透明导航栏
            StarusUtil.StatusBarLightMode(this);//字体改变
        }

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mContext = this;
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击键盘空白处 消失键盘
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    /*********************
     * 子类实现
     *****************************/
    //获取布局文件
    public abstract int getLayoutId();


    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
        public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 开启浮动加载进度条
     */
    public void startProgressDialog() {
        LoadingDialog.showDialogForLoading(this);
    }

    /**
     * 开启浮动加载进度条
     *
     * @param msg
     */
    public void startProgressDialog(String msg) {
//        LoadingDialog.showDialogForLoading(this, msg, true);
    }

    /**
     * 停止浮动加载进度条
     */
    public void stopProgressDialog() {
        LoadingDialog.cancelDialogForLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("Resume");
//        友盟统计
//        MobclickAgent.onResume(this);

        if (!Global.isActive) {
            //app 从后台唤醒，进入前台
            LogUtil.d("进入前台");
            Global.isActive = true;
            LocationUtils.Companion.location();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("pause");
        //友盟统计
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("Stop");
        if (!PackageUtils.isAppOnForeground(this)) {
            //app 进入后台
            LogUtil.d("进入后台");

            Global.isActive = false;//全局变量isActive = false 记录当前已经进入后台
            LocationUtils.Companion.stopLocation();
        }
    }

    @Override
    public void onBackPressed() {
        AppManager.getAppManager().finishActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("Destory");
        mRxManager.clear();
        ButterKnife.unbind(this);
        AppManager.getAppManager().finishActivity(this);
    }

    public void setTop(boolean isShowTop){
        if(isShowTop){
            if (Build.VERSION.SDK_INT >= 23) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setNavigationBarColor(Color.TRANSPARENT);//底部状态栏的改变
                getWindow().setStatusBarColor(Color.TRANSPARENT);//顶部状态的改变
            }
        }
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
    }


    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
