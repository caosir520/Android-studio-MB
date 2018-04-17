package net.ed58.dlm.clients.login;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wise.common.baseapp.AppManager;
import com.wise.common.commonutils.FormatUtil;
import com.wise.common.commonutils.StarusUtil;
import com.wise.common.commonutils.ToastUtil;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.base.BaseCoreMVPActivity;
import net.ed58.dlm.clients.base.BaseUI;
import net.ed58.dlm.clients.main.MainActivity;
import net.ed58.dlm.clients.sp.SPKey;
import net.ed58.dlm.clients.sp.SettingUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 创建人: caosir
 * 创建时间：2017/10/18
 * 修改人：
 * 修改时间：
 * 类说明：
 */

public class LoginActivity extends BaseCoreMVPActivity implements LoginContract.UI {



    @Bind(R.id.et_number)
    EditText et_number;
    @Bind(R.id.et_yanzhengma)
    EditText et_yanzhengma;
    @Bind(R.id.tv_getyanzhengma)
    TextView tv_yanchengma;
    @Bind(R.id.bt_login)
    Button bt_login;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresent createPresenter() {
        return new LoginPresent(this,this);
    }

    @Override
    protected BaseUI getUi() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        //透明状态栏

        if(StarusUtil.StatusBarLightMode(this)!=0){
            StarusUtil.transparencyBar(this);
            StarusUtil.StatusBarLightMode(this);
        }


    }

    //发送验证码的点击事件
    @OnClick(R.id.tv_getyanzhengma)
    public void getYanZhenMa(TextView textView){
        String number = et_number.getText().toString();
        ;
        if("".equals(number)){
            ToastUtil.showShort(this,"手机号码不得为空");
        }else if(!FormatUtil.isMobileNO(number)){
            ToastUtil.showShort(this,"手机号码格式错误");
        }else{
            ((LoginPresent)getPresenter()).sendYanZhenMa(number);
        }

    }
    @OnClick(R.id.bt_login)
    public void btLogin(Button button){
        String number = et_number.getText().toString();
        String code = et_yanzhengma.getText().toString();


        if("".equals(number)){
            ToastUtil.showShort(this,"手机号码不得为空");
        }else if(!FormatUtil.isMobileNO(number)) {
            ToastUtil.showShort(this, "手机号码格式错误");
        }else if ("".equals(code)){
            ToastUtil.showShort(this, "验证码不能为空");
        }else{
            ((LoginPresent)getPresenter()).login(number,code);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void showTextTime(String string) {
        tv_yanchengma.setEnabled(false);
        tv_yanchengma.setText(string);
        tv_yanchengma.setTextColor(Color.BLACK);
    }

    @Override
    public void showLoginSuccess() {
        //保存登陆信息
        SettingUtils.setEditor(this, SPKey.KEY_FINISH_FILL_INFO,true);
        Toast.makeText(this,"登陆成功",Toast.LENGTH_SHORT).show();
        startActivity(MainActivity.class);
        AppManager.getAppManager().finishActivity();
    }

    @Override
    public void showLoginError() {
        Toast.makeText(this,"验证码错误，请重新输入",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTextSend() {
        tv_yanchengma.setEnabled(true);
        tv_yanchengma.setText(getResources().getString(R.string.login_getyanzhengma));
        tv_yanchengma.setTextColor(getResources().getColor(R.color.getyanzhengma));
    }
}
