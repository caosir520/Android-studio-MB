package net.ed58.dlm.clients.adapter;

import android.content.Context;
import android.view.View;

import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.entity.Insured;
import net.ed58.dlm.clients.main.MainMeFragment;

import java.util.List;

/**
 *
 * 我的页面的适配器
 * Created by Administrator on 2017/11/21/021.
 */

public class MyAdapter extends CommonRecycleViewAdapter<MainMeFragment.item> {
    public MyAdapter(Context context, int layoutId, List<MainMeFragment.item> mDatass) {
        super(context, R.layout.item_my, mDatass);
    }

    @Override
    public void convert(ViewHolderHelper helper, MainMeFragment.item item) {

        helper.setText(R.id.textView1,item.getText());
        if(item.getText2().equals("")){
            helper.setVisible(R.id.textView2,false);
        }
        helper.setText(R.id.textView2,item.getText2());

        helper.setBackgroundRes(R.id.image1,item.getImage());

        if(item.getImage()==0){
            helper.getConvertView().setVisibility(View.GONE);
        }

    }
}
