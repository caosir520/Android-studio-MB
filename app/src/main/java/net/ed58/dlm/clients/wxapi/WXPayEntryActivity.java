package net.ed58.dlm.clients.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wise.common.baserx.RxBus;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.global.Constant;
import net.ed58.dlm.clients.global.RxKey;
import net.ed58.dlm.clients.util.RepeatInterfaceUtils;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);
        api = WXAPIFactory.createWXAPI(this, getResources().getString(R.string.wx_share_appid));
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.errCode == 0) {
            if (Constant.PAY_POSITION == Constant.PAY_FROM_WALLET) {
                Toast.makeText(this, "充值成功", Toast.LENGTH_SHORT).show();
                RxBus.getInstance().post(RxKey.EVENT_WALLET_CHANGE,true);
                finish();
                return;
            }
            RepeatInterfaceUtils.INSTANCE.paySuccess(this);
        } else if (resp.errCode == -1) {
            Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
        } else if (resp.errCode == -2) {
            Toast.makeText(this, "取消了支付", Toast.LENGTH_SHORT).show();
//            RepeatInterfaceUtils.cancelPay(this, Constant.PAY_DETAIL_ID);
        }

        Constant.PAY_POSITION = -1;
        Constant.PAY_DETAIL_ID = "";
        finish();

    }
}
