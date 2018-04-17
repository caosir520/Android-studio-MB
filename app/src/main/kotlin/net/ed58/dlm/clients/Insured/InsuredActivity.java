package net.ed58.dlm.clients.Insured;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.wise.common.baseapp.AppManager;
import com.wise.common.commonutils.LogUtil;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.adapter.InsuredAdapter;
import net.ed58.dlm.clients.base.BaseCoreMVPActivity;
import net.ed58.dlm.clients.entity.Insured;

import java.util.List;
import java.util.Stack;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 创建人: caosir
 * 创建时间：2017/11/1
 * 修改人：
 * 修改时间：
 * 类说明：保价信息的Activity
 */

public class InsuredActivity extends BaseCoreMVPActivity<InsuredPresent,InsuredPresent.UI> implements InsuredPresent.UI  {


    @Bind(R.id.recyclerView3)
    RecyclerView recyclerView;
    @Bind(R.id.button_submit)
    Button bt_Submit;
    InsuredAdapter adapter;
    @Bind(R.id.radioButton)
    TextView ra_button;



    //堆，用于实现单选模式
    private Stack<Integer> historyItemSelect = new Stack<>();

    @Override
    protected InsuredPresent createPresenter() {
        return new InsuredPresent(this);
    }

    @Override
    protected InsuredPresent.UI getUi() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ra_button.setSelected(true);
        bt_Submit.setEnabled(ra_button.isSelected());
        getPresenter().getInsuredList("0","10");
    }


    @OnClick(R.id.radioButton)
    public void RadioClick(){
        ra_button.setSelected(!ra_button.isSelected());
        LogUtil.d("raisSelect"+ra_button.isSelected());
        bt_Submit.setEnabled(ra_button.isSelected());

    }
    @OnClick(R.id.im_back)
    public void BackOnClick(){
        AppManager.getAppManager().finishActivity();
    }

    @OnClick(R.id.button_submit)
    public void onSubmit(){
        int i ;
        Intent intent = new Intent();
        if(!historyItemSelect.empty()){
            i = historyItemSelect.pop();
            intent.putExtra("insuranceFee",adapter.get(i).getInsuranceFee());
            intent.putExtra("supportValueId",adapter.get(i).getId());
        }else{
            i = -1;
        }
        intent.putExtra("clickPos",i);
        setResult(RESULT_OK,intent);
        AppManager.getAppManager().finishActivity();
    }
    @Override
    public void showHostoryList(List<Insured.ListEntity> list) {
        LogUtil.d("显示数据");
        int i = getIntent().getIntExtra("clickPos",-1);
        if(i!=-1) {
            list.get(i).setSelect(true);
            historyItemSelect.push(i);
        }
        adapter = new InsuredAdapter(this,R.layout.insured_service_item,list);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                LogUtil.d("viewID"+view.isSelected());
                ViewGroup view1 = (ViewGroup) view;
                //重堆中获取上次的点击位子
                if(!historyItemSelect.empty()){
                    //当堆不为空的时候

                    int i = historyItemSelect.pop();
                    //修改上次数据标识
                    adapter.get(i).setSelect(!adapter.get(i).isSelect());
                    //修改上次item的图标
                    adapter.notifyItemChanged(i);
                    if(i==position){
                        //如果双选就去取消选中，退出页面
                        return;
                    }
                }

                //修改本次图标
                ImageView image = (ImageView) view1.findViewById(R.id.im_insured_select);
                adapter.get(position).setSelect(!adapter.get(position).isSelect());
                image.setSelected(adapter.get(position).isSelect());
                //这次数据压栈
                historyItemSelect.push(position);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
        recyclerView.setX();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }



    @Override
    public int getLayoutId() {
        return R.layout.activity_insured_service;
    }


    public static void startActivity(Activity activity,int position){
        Intent intent = new Intent(activity,InsuredActivity.class);
        intent.putExtra("clickPos",position);
        activity.startActivity(intent);
    }



}
