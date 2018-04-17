package net.ed58.dlm.clients.sp;

/**
 * SharedPreferences文件中保存的Key都在这个地方定义
 * @author wangheng
 */
public class SPKey {

    /** 登录标示**/
    public static final String KEY_ACCESS_COOKIE = "access_cookie";
    public static final String KEY_FINISH_FILL_INFO = "finish_fill_info";
    public static final String KEY_USER_NICKNAME = "nickName";
    public static final String KEY_USER_PHONE = "userPhone";
    public static final String KEY_USER_ICON = "userIcon";
    public static final String KEY_USER_SEX = "userSex";
    public static final String KEY_USER_BIRTH = "userBirth";
    public static final String KEY_USER_NUM = "user_num";//用户各个状态消息数量

    public static final String KEY_USER_ID = "user_id";//用户id

    public static final String KEY_SHOP_ID = "shop_id";
    public static final String KEY_SHOP_NAME = "shop_name";

    public static final String KEY_KEFU_PHONE = "kefu_phone";

    public static final String KEY_MLAT = "mlat";//我的经度
    public static final String KEY_MLNG = "mlng";//我的纬度



    /** 快捷方式存在的KEY **/
    public static final String KEY_SHORT_CUT_EXISTS = "shortCutExists";

    /** userId的KEY **/
    public static final String KEY_UID = "uid";

    /** sessionId的KEY **/
    public static final String KEY_DEVICE_ID = "deviceId";

    /** 有版本升级**/
    public static final String KEY_HAS_VERSION_UPDATE = "key_has_version_update";

    /** 上一次记录的内存级别 **/
    public static final String KEY_APPMEMERY_LEVEL = "AppMemeryLevel";


}
