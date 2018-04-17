/*
 * Copyright 2015 Wicresoft, Inc. All rights reserved.
 */

package net.ed58.dlm.clients.pay;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.ed58.dlm.clients.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Description of WXPayHelper
 *
 */
public class WXPayHelper {
    private StringBuffer sb = new StringBuffer();
    private Context context;
    private PayReq req;
    private IWXAPI msgApi;
    private String prepayid;

    private String APPID;
    private String MCH_ID;
    private String API_KEY;

    public WXPayHelper(Context context, String prepayid) {
        this.context = context;
        this.prepayid = prepayid;

        APPID = context.getResources().getString(R.string.wx_share_appid);
        MCH_ID = context.getResources().getString(R.string.wx_pay_mch_id);
        API_KEY = context.getResources().getString(R.string.wx_pay_appkey);
    }

    public void pay() {
        req = new PayReq();
        msgApi = WXAPIFactory.createWXAPI(context, APPID, true);
        msgApi.registerApp(APPID);
        // String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        // new GetPrepayIdTask().execute();
        if (!msgApi.isWXAppInstalled()) {
            Toast.makeText(context, "请安装微信客户端", Toast.LENGTH_SHORT).show();
        } else
            genPayReq();
    }

    public Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 10 / 100;
    }

    private void genPayReq() {

        req.appId = APPID;
        req.partnerId = MCH_ID;
        // 服务端获取
        // req.prepayId = resultunifiedorder.get("prepay_id");
        req.prepayId = prepayid;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        String stringA =
                "appid=" + req.appId
                        + "&noncestr=" + req.nonceStr
                        + "&package=" + req.packageValue
                        + "&partnerid=" + req.partnerId
                        + "&prepayid=" + prepayid
                        + "&timestamp=" + req.timeStamp;


        String stringSignTemp = stringA + "&key=" + API_KEY;
        String sign = MD5.getMessageDigest(stringSignTemp.getBytes()).toUpperCase();

        req.sign = sign;

        sendPayReq();
    }

    private void sendPayReq() {
        msgApi.registerApp(APPID);
        msgApi.sendReq(req);
    }
}
