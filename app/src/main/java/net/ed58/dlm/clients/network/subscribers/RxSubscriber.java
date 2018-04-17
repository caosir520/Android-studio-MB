package net.ed58.dlm.clients.network.subscribers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.wise.common.baseapp.AppManager;
import com.wise.common.commonutils.NetWorkUtils;
import com.wise.common.commonutils.ToastUtil;
import com.wise.common.commonwidget.LoadingDialog;
import com.wise.common.commonwidget.ProgressCancelListener;

import net.ed58.dlm.clients.BuildConfig;
import net.ed58.dlm.clients.MyApplication;
import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.global.Constant;
import net.ed58.dlm.clients.login.LoginActivity;
import net.ed58.dlm.clients.network.ResultException;
import net.ed58.dlm.clients.sp.MyConfiguration;

import rx.Subscriber;

/**
 * des:订阅封装
 * Created by xsf
 * on 2016.09.10:16
 */

public abstract class RxSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private Context mContext;
    private String msg;
    private boolean showDialog = true;

    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog = true;
    }

    public void hideDialog() {
        this.showDialog = true;
    }

    public RxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public RxSubscriber(Context context) {
        this(context, MyApplication.getContext().getString(R.string.loading), true);
    }

    public RxSubscriber(Context context, boolean showDialog) {
        this(context, MyApplication.getContext().getString(R.string.loading), showDialog);
    }

    //中止关闭对话框
    @Override
    public void onCompleted() {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (showDialog) {
            try {
                LoadingDialog.showDialogForLoading((Activity) mContext, msg, true, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        //网络
        if (!NetWorkUtils.isNetConnected(MyApplication.getContext())) {
            //弹吐司
            ToastUtil.showBottomtoast(MyApplication.getContext(), MyApplication.getContext().getString(R.string.no_net));
            _onError(Constant.NETWORK_ERROR, null);
        }
        //服务器 ，这个异常是哪里抛出来的？
        else if (e instanceof ResultException) {
            ResultException resultException = (ResultException) e;
            ToastUtil.showBottomtoast(MyApplication.getContext(), resultException.getMessage());

            if (resultException.getErrCode() == 1 || resultException.getErrCode() == 10 || resultException.getErrCode() == 10006 || resultException.getErrCode() == 10002) {
                //00000001您还未登录或者登录已超时,请重新登录
                //00000010,这个账号在别的设备登陆
                //10002用户停用
                //10006登录过期或没登录
                MyConfiguration.getInstance().removeSession(mContext);

                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                AppManager.getAppManager().finishAllActivity();
//
            } else {

                _onError(resultException.getErrCode(), resultException.getMessage());
            }
        }
        //其它
        else {
            ToastUtil.showBottomtoast(MyApplication.getContext(), MyApplication.getContext().getString(R.string.net_error));
            _onError(Constant.NETWORK_ERROR, "");
        }
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(int code, String message);


    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}
