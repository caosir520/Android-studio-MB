/*
 * Copyright 2015 Wicresoft, Inc. All rights reserved.
 */

package net.ed58.dlm.clients.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.wise.common.alipay.PayResult;
import com.wise.common.baserx.RxBus;
import com.wise.common.commonutils.LogUtil;

import net.ed58.dlm.clients.global.Constant;
import net.ed58.dlm.clients.global.RxKey;
import net.ed58.dlm.clients.util.RepeatInterfaceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


/**
 * Description of AliPayhelper
 *
 */
public class AliPayhelper {
    private Context context;
    private String payInfo;
    private static final int SDK_PAY_FLAG = 1;


    public AliPayhelper(Context context, String payInfo) {
        this.context = context;
        this.payInfo = payInfo;
    }

    public void pay() {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);


                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();
                    LogUtil.d("支付宝支付" + resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        if (Constant.PAY_POSITION == Constant.PAY_FROM_WALLET) {
                            Toast.makeText(context, "充值成功", Toast.LENGTH_SHORT).show();
                            RxBus.getInstance().post(RxKey.EVENT_WALLET_CHANGE,true);
                            return;
                        }
                        RepeatInterfaceUtils.INSTANCE.paySuccess(context);
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "" +
                                    "取消了支付", Toast.LENGTH_SHORT).show();
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                            Intent intent = new Intent(context, FlashBuyPayFinishActivity.class);
//                            intent.putExtra("oderState", false);
//                            context.startActivityForResult(intent);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            Constant.PAY_POSITION = -1;
            Constant.PAY_DETAIL_ID = "";
        }
    };


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
//    public String sign(String content) {
//        return SignUtils.sign(content, RSA_PRIVATE);
//    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }
}
