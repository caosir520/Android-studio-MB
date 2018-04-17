package net.ed58.dlm.clients.global;

/**
 * Created by sunpeng on 17/2/13.
 */
/**
 * 说明:返回码
*@author: caosir
*@time：2017/10/29
*@param：
*@return
*/
public class   Constant {

    //activity request code
    public static final int REQUEST_FIRST_CODE = 1;
    public static final int REQUEST_SECOND_CODE = 2;
    public static final int REQUEST_THIRD_CODE = 3;
    public static final int REQUEST_FOUR_CODE = 4;
    public static final int REQUEST_FIVE_CODE = 5;

    public static final int REQUEST_SELECT_IAMGE_CODE = 1001;//单张图片 拍照和选择图片的码

    public static final int REQUEST_MULTI_SELECT_IAMGE_CODE = 1002;//选择多张图片
    public static final int REQUEST_MULTI_TAKE_PHOTO_CODE = 1003;//和多张图片一起的拍照


    public static final int NETWORK_ERROR = 0x1;//网络错误码

    public static int PAY_POSITION = -1;//支付位置
    public static String PAY_DETAIL_ID = "";//取消支付时调用的ID
    public static final int PAY_FROM_SUBMIT = 10;//正常下单
    public static final int PAY_FROM_WAIT_PAY_LIST = 11;//代付款订单列表
    public static final int PAY_FROM_ALL_ORDER = 12;//全部订单列表 待支付
    public static final int PAY_FROM_WALLET = 13;//充值钱包

    public static final int WEBVIEW_TYPE_USER_LICENSE = 100001;//加载软件使用许可协议
    public static final int WEBVIEW_TYPE_TOPIC = 100002;//加载专题

    public static final int BEHAVIOR_GOODS_APPRAISE = 5;
    public static final int BEHAVIOR_GOODS_QUESTION = 6;
    public static final int BEHAVIOR_JOIN_CART = 8;
}
