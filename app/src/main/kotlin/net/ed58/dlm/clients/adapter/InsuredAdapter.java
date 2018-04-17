package net.ed58.dlm.clients.adapter;

import android.content.Context;

import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;

import net.ed58.dlm.clients.R;
import net.ed58.dlm.clients.entity.Insured;

import java.util.List;

/**
 * 创建人: caosir
 * 创建时间：2017/10/31
 * 修改人：
 * 修改时间：
 * 类说明：
 */

public class InsuredAdapter extends CommonRecycleViewAdapter<Insured.ListEntity> {
    public InsuredAdapter(Context context, int layoutId, List<Insured.ListEntity> mDatass) {
        super(context, R.layout.insured_service_item, mDatass);
    }

    public InsuredAdapter(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    public void convert(ViewHolderHelper helper, Insured.ListEntity listEntity) {
        //这里写适配器，
        helper.setText(R.id.tv_insured_title,""+listEntity.getInsuranceFee()+"元保价");
        helper.setText(R.id.tv_insured_other,"若物品出现损坏，最高可获得"+listEntity.getCompensateFee()+"元赔付");
        helper.getView(R.id.im_insured_select).setSelected(listEntity.isSelect());
    }
}
