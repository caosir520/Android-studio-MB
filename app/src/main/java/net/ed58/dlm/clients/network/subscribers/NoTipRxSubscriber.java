package net.ed58.dlm.clients.network.subscribers;

import android.app.Activity;
import android.content.Context;

import com.wise.common.commonwidget.LoadingDialog;
import com.wise.common.commonwidget.ProgressCancelListener;

import net.ed58.dlm.clients.BuildConfig;
import net.ed58.dlm.clients.MyApplication;
import net.ed58.dlm.clients.R;

import rx.Subscriber;

/**
 * des:订阅封装
 * Created by xsf
 * on 2016.09.10:16
 */

public abstract class NoTipRxSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

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

    public NoTipRxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public NoTipRxSubscriber(Context context) {
        this(context, MyApplication.getContext().getString(R.string.loading), true);
    }

    public NoTipRxSubscriber(Context context, boolean showDialog) {
        this(context, MyApplication.getContext().getString(R.string.loading), showDialog);
    }


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
