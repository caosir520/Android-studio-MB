package net.ed58.dlm.clients.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.wise.common.commonutils.LogUtil;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.entity.Address;

import java.util.List;

/**
 * 创建人: caosir
 * 创建时间：2017/10/30
 * 修改人：
 * 修改时间：
 * 类说明：
 */

public class HistoryAdapter extends CommonRecycleViewAdapter<Address.ListEntity> {

    public HistoryAdapter(Context context, int layoutId, List<Address.ListEntity> mDatass) {
        super(context, R.layout.history_address_item, mDatass);
    }

    public HistoryAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }
    //装配数据
    @Override
    public void convert(ViewHolderHelper helper, Address.ListEntity listEntity) {
        helper.setText(R.id.tv_history_item_name,listEntity.getName()+"    "+ listEntity.getTel());
        helper.setText(R.id.tv_history_item_address,listEntity.getAddress());
        helper.getView(R.id.im_select).setSelected(listEntity.isIsDefault());
    }


    boolean is = false;

    public void setIs(boolean s){
        is=s;
        int i = 13;
        setIs(!is);
    }
}
