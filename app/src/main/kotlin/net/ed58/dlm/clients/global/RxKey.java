package net.ed58.dlm.clients.global;

/**
 * Created by sunpeng on 17/8/8.
 */

public class RxKey {
    public static final String EVENT_WELCOME_INIT_COMPLETED="EVENT_WELCOME_INIT_COMPLETED";//欢迎页初始化

    public static final String EVENT_UPDATE_MAIN_BOTTOM_ADDRESS="EVENT_WELCOME_INIT_COMPLETED";//更新首页下面的地址信息

    public static final String EVENT_ORDER_FINISH="EVENT_ORDER_FINISH";//订单完成恢复首页的状态

    public static final String EVENT_SELECT_COMMON_ADDRESS="EVENT_SELECT_COMMON_ADDRESS";//选择常用地址

    public static final String EVENT_PAY_GO_MAIN_ORDER_TAB ="EVENT_PAY_GO_MAIN_ORDER_TAB";//支付成功或者下单成功后 切换主页TAB 到订单tab；0是待支付，1是待接单
    public static final String EVENT_GO_MY_RECEIVE_ORDER_LIST ="EVENT_GO_MY_RECEIVE_ORDER_LIST";//跳转到我的接单列表

    public static final String EVENT_CANCEL_SEND_LIST_ITEM="EVENT_CANCEL_SEND_LIST_ITEM";//取消发单列表的item
    public static final String EVENT_ALL_SEND_LIST_ITEM="EVENT_ALL_SEND_LIST_ITEM";//在我的发单更多列表操作过 改变状态直接刷新列表
    public static final String EVENT_REMOVE_RECEIVE_LIST_ITEM ="EVENT_REMOVE_RECEIVE_LIST_ITEM";//无法配送列表的item
    public static final String EVENT_ALL_RECEIVE_LIST_ITEM ="EVENT_ALL_RECEIVE_LIST_ITEM";//在我的更多接单列表操作过 改变状态直接刷新列表

    public static final String EVENT_UPDATE_WITHDRAW_ACCOUNT="EVENT_UPDATE_WITHDRAW_ACCOUNT";//及时更新修改过的提现账号
    public static final String EVENT_WALLET_CHANGE ="EVENT_WALLET_CHANGE";//钱包金额改变 刷新
    public static final String EVENT_ME_UPDATE="EVENT_ME_UPDATE";//用户更新资料
}
