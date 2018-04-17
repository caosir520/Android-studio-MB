package net.ed58.dlm.clients.Insured;

import net.ed58.dlm.clients.base.BaseCoreMVPActivity;
import net.ed58.dlm.clients.base.BasePresenter;
import net.ed58.dlm.clients.base.BaseUI;
import net.ed58.dlm.clients.entity.Insured;
import net.ed58.dlm.clients.network.http.HttpMethods;
import net.ed58.dlm.clients.network.subscribers.RxSubscriber;

import java.util.List;

/**
 * 创建人: caosir
 * 创建时间：2017/11/1
 * 修改人：
 * 修改时间：
 * 类说明：保价的逻辑
 */

public class InsuredPresent extends BasePresenter<InsuredPresent.UI> {

    public InsuredPresent(InsuredActivity insuredActivity){
        onUiReady(insuredActivity,insuredActivity);
    }

    public void getInsuredList(String start,String limit){
        HttpMethods httpMethods  =HttpMethods.getInstance();
        RxSubscriber<Insured> subscriber = new RxSubscriber<Insured>(getActivity()) {
            @Override
            protected void _onNext(Insured insured) {
                //返回成功数据
                getUi().showHostoryList(insured.getList());
            }

            @Override
            protected void _onError(int code, String message) {

            }
        };
        httpMethods.getInsuredList(subscriber,"0","10");


    }

    interface UI extends BaseUI{
        public  void showHostoryList(List<Insured.ListEntity> list);
    }

}
