package net.ed58.dlm.clients.view.numbervcodeview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.ed58.dlm.clients.R;


/**
 * Created by linkaipeng on 16/8/2.
 */
public class CenterNumberCodeView extends BaseNumberCodeView implements View.OnClickListener {

    private LinearLayout mBottomNumberCodeLayout;
    private ImageView mCloseImageView;
    private OnHideBottomLayoutListener mOnHideBottomLayoutListener;
    private float mDisplayHeight;

    public CenterNumberCodeView(Context context) {
        super(context, null);
    }

    public CenterNumberCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected View createView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_center_password, null);
        mCloseImageView = (ImageView) view.findViewById(R.id.im_back);
        mCloseImageView.setOnClickListener(this);
        return view;
    }

    @Override
    protected void onResult(String code) {
        if (mCallback != null) {
            mCallback.onResult(code);
        }
    }

    public void setOnHideBottomLayoutListener(OnHideBottomLayoutListener onHideLayoutListener) {
        mOnHideBottomLayoutListener = onHideLayoutListener;
    }

    @Override
    public void restoreViews() {
        super.restoreViews();
    }

    @Override
    public void onClick(View v) {
        mOnHideBottomLayoutListener.onHide();
    }

    public interface OnHideBottomLayoutListener {
        void onHide();
    }
}
