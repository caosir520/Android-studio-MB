package net.ed58.dlm.clients.login;

import net.ed58.dlm.clients.base.BasePresenter;
import net.ed58.dlm.clients.base.BaseUI;

/**
 * 创建人: caosir
 * 创建时间：2017/10/18
 * 修改人：
 * 修改时间：
 * 类说明：登陆模块合同层
 */

public class LoginContract {
    public interface UI extends BaseUI{

        public void  showTextTime(String string);
        public void showLoginSuccess();
        public void showLoginError();
        public void showTextSend();

    }
    public abstract static class Present extends BasePresenter<LoginContract.UI>{
        public abstract void sendYanZhenMa(String number);

        public abstract void login(String number,String YanZhenMa);
    }
}
