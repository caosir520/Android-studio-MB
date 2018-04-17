package net.ed58.dlm.clients.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener
import com.bumptech.glide.Glide
import com.wise.common.baseapp.AppManager
import com.wise.common.baseglide.CircleCrop
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.LogUtil
import kotlinx.android.synthetic.main.fragment_me.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.MyAdapter
import net.ed58.dlm.clients.base.BaseCoreMVPFragment
import net.ed58.dlm.clients.entity.CustomerM
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.login.LoginActivity
import net.ed58.dlm.clients.me.MeInfoActivity
import net.ed58.dlm.clients.sp.MyConfiguration
import net.ed58.dlm.clients.wallet.MyWalletActivity


/**
 * Created by sunpeng on 17/2/10.
 */

class MainMeFragment : BaseCoreMVPFragment<MainMePresent, MainMePresent.IMainMe>(), MainMePresent.IMainMe {
    override fun showMe(date: CustomerM) {
        Glide.with(this).load(date.head).asBitmap()
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .transform( CircleCrop(activity))
                .into(im_my)
        Glide.with(this).load(date.head).asBitmap()
                .placeholder(R.mipmap.icon_default_avatar)
                .error(R.mipmap.icon_default_avatar)
                .transform( CircleCrop(activity))
                .into(imageView)
        coll_top.title=date.nickName
        toolbar.setTitleTextColor(resources.getColor(R.color.black))
    }

    private var isMy:Boolean=false //标题头像默认不显示
    private var mView: View? = null
    private val MIN_AVATAR_PERCENTAGE_SIZE = 0.3f
    private val EXTRA_FINAL_AVATAR_PADDING = 80

    private var mStartYPosition: Int = 0 // 起始的Y轴位置
    private var mFinalYPosition: Int = 0 // 结束的Y轴位置
    private var mStartHeight: Int = 0 // 开始的图片高度
    private var mFinalHeight: Int = 0 // 结束的图片高度
    private var mStartXPosition: Int = 0 // 起始的X轴高度
    //private var mFinalXPosition: Int = 0 // 结束的X轴高度
    private var mStartToolbarPosition: Float = 0.toFloat() // Toolbar的起始位置

    override fun createPresenter(): MainMePresent {
        return MainMePresent()
    }

    override fun getUi(): MainMePresent.IMainMe {
        return this
    }


    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_me, container, false)
        }
        return mView!!
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getMe()
        setHasOptionsMenu(true)
        initRecycler()
        offListener()
        imageView.setOnClickListener{
            MyConfiguration.getInstance().removeSession(activity)

            val intent = Intent()
            intent.setClass(activity, LoginActivity::class.java)
            startActivity(intent)
            AppManager.getAppManager().finishAllActivity()
        }

        //点击Appbar 进入资料修改
        appBar.setOnClickListener{
            MeInfoActivity.atartActivity(activity)
        }

        mRxManager.on<List<String>>(RxKey.EVENT_ME_UPDATE){
            list->
            var date=CustomerM()
            date.head=list.get(0)
            date.nickName=list.get(1)
            showMe(date)
        }

    }
    //设置Appbar滑动监听
    private fun offListener(){
        appBar.addOnOffsetChangedListener({appBarLayout, verticalOffset ->
            initImageViewDate()
            var maxScroll= appBarLayout.totalScrollRange;//获取滑动的最高高度
            var percentage:Float = (Math.abs(verticalOffset).toFloat())/maxScroll.toFloat() //获取滑动的百分比
            LogUtil.e(""+percentage)
            setImageView(percentage)
        })
    }


    //更具滑动百分比更改头像大小和透明度，体现一种淡入淡出的效果
    private fun setImageView(percentage:Float){
        // 图片位置
        imageView.setY(mStartYPosition*(1f-percentage))
        //  LogUtil.e("Y="+(mStartYPosition*(1f-percentage)))
        imageView.setX(mStartXPosition.toFloat())

        // 图片大小
        imageView.scaleX=(1f-percentage)
        imageView.scaleY=(1f-percentage)
        imageView.setAlpha((1f-percentage))
        if(percentage>0.8f){
            im_my.visibility=View.VISIBLE
            im_my.scaleY=percentage
            im_my.scaleX=percentage
            im_my.alpha=percentage
        }else{
            im_my.visibility=View.GONE
        }

    }

    override fun onResume() {
        super.onResume()
        mView!!.setFitsSystemWindows(true);

        //  StarusUtil.setViewFit(toolbar,activity)
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    override fun onHiddenChanged(hidden: Boolean) {
        if (mView != null) {
            if (hidden) {
                mView!!.setFitsSystemWindows(false);
            } else {
                mView!!.setFitsSystemWindows(true);
            }
            mView!!.requestApplyInsets();
        }
        super.onHiddenChanged(hidden)
    }

    //初始化头像控件的距离参数
    private fun initImageViewDate() {
        // 图片控件中心
        if (mStartYPosition == 0)
            mStartYPosition = (imageView.getY() ).toInt();
        // 图片控件水平中心 view.getx 是view的左上角坐标，平移后会进行改变
        if (mStartXPosition == 0)
            mStartXPosition = (imageView.getX()).toInt();

        // Toolbar中心
        if (mFinalYPosition == 0)
            mFinalYPosition = (toolbar.getHeight() / 2);

        // 图片高度
        if (mStartHeight == 0)
            mStartHeight = imageView.getHeight();

        // Toolbar缩略图高度 ，一直缩小到最小，所以就是0

        // Toolbar的起始位置
        if (mStartToolbarPosition == 0.toFloat())
            mStartToolbarPosition = toolbar.getY() + (toolbar.getHeight() / 2);
    }



    private fun initRecycler() {
        val item1 = item(R.mipmap.icon_news, "通知", "5");
        val item2 = item(R.mipmap.icon_my_wallet, "我的钱包", "");
        val item3 = item(R.mipmap.icon_invite_friends, "邀请好友", "");
        val item4 = item(R.mipmap.icon_evaluation_records, "评价记录", "");
        val item5 = item(R.mipmap.icon_rule_description, "规则说明", "");
        val item6 = item(R.mipmap.icon_report_complaint, "投诉举报", "");
        val item7 = item(R.mipmap.icon_feedback, "意见反馈", "");
        val item8 = item(R.mipmap.icon_service_hotline, "客服热线", "");
        val item9 = item(R.mipmap.icon_knight_college, "骑士学院", "");
        val item10 = item(R.mipmap.icon_set, "设置", "");
        val item11 = item(0, "设置", "");
        val listItem = listOf<item>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10,item11)
        my_recyclerview.isNestedScrollingEnabled = false
        my_recyclerview.layoutManager = LinearLayoutManager(activity)
        val myAdapter = MyAdapter(activity, R.layout.item_my, listItem)
        myAdapter.setOnItemClickListener(object : OnItemClickListener<item> {
            override fun onItemClick(parent: ViewGroup?, view: View?, t: item?, position: Int) {
                if (position == 1) {
                    MyWalletActivity.startActivity(activity)
                }
            }

            override fun onItemLongClick(parent: ViewGroup?, view: View?, t: item?, position: Int): Boolean {
                return false
            }


        })
        my_recyclerview.adapter = myAdapter

    }




    companion object {
        class item (image:Int,text:String,text2:String){
            var image:Int?=0
            var text:String?=""
            var text2:String?=""
            init {
                this.image=image;
                this.text=text;
                this.text2=text2;
            }

        }
    }

}
