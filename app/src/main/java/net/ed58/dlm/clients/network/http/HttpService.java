package net.ed58.dlm.clients.network.http;



import net.ed58.dlm.clients.entity.AccountBean;
import net.ed58.dlm.clients.entity.AccountLogM;
import net.ed58.dlm.clients.entity.Address;
import net.ed58.dlm.clients.entity.Comments;
import net.ed58.dlm.clients.entity.ComplaintBean;
import net.ed58.dlm.clients.entity.ComplaintReason;
import net.ed58.dlm.clients.entity.CustomerBean;
import net.ed58.dlm.clients.entity.CustomerM;
import net.ed58.dlm.clients.entity.Insured;
import net.ed58.dlm.clients.entity.MainReceiveOrderBean;
import net.ed58.dlm.clients.entity.OrderListBean;
import net.ed58.dlm.clients.entity.OrderM;
import net.ed58.dlm.clients.entity.PageResultList;
import net.ed58.dlm.clients.entity.PayBean;
import net.ed58.dlm.clients.entity.PreOrderBean;
import net.ed58.dlm.clients.entity.ProductTypeListBean;
import net.ed58.dlm.clients.entity.ReviewBean;
import net.ed58.dlm.clients.entity.ReviewM;
import net.ed58.dlm.clients.entity.SingleBoolValue;
import net.ed58.dlm.clients.entity.SingleIntValue;
import net.ed58.dlm.clients.entity.SingleStringValue;
import net.ed58.dlm.clients.entity.UploadImagesBean;
import net.ed58.dlm.clients.entity.WithdrawAccountBean;
import net.ed58.dlm.clients.network.entity.HttpResult;

import java.math.BigDecimal;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 说明: 连接自己的服务器的基础接口
 *
 * @author: caosir
 * @time：2017/10/29
 * @param：
 * @return
 */
public interface HttpService {


    @FormUrlEncoded
    @POST("client/ClientUserLogin/mobileLogin")
    Observable<HttpResult<CustomerBean>> login(@Field("mobile") String mobile, @Field("code") String code);

    @FormUrlEncoded
    @POST("client/ClientUserLogin/sendLoginSmsCode")
    Observable<HttpResult<SingleIntValue>> sendLoginSmsCode(@Field("mobile") String mobile);//发短信

    @GET("client/ClientUserLogin/logout")
    Observable<HttpResult<SingleBoolValue>> logout();

    //获取历史地址
    @GET("client/ClientAddress/list")
    Observable<HttpResult<Address>> getHostoryAddressList(@Query("start") String start, @Query("limit") String limit);


    @GET("client/ClientCategory/listCache")
    Observable<HttpResult<ProductTypeListBean>> getProductType(@Query("type") int type);//type 1和2获取下单品类，3获取充值金额类型

    @FormUrlEncoded
    @PUT("client/ClientOrder/add")
    Observable<HttpResult<PreOrderBean>> submitOrder(@FieldMap Map<String, Object> map);

    @GET("client/ClientOrder/time")
    Observable<HttpResult<SingleStringValue>> getCurrentTime();

    //获取报价列表
    @GET("client/ClientSupportValue/list")
    Observable<HttpResult<Insured>> getInsuredList(@Query("start") String start, @Query("limit") String limit);

    @FormUrlEncoded
    @PUT("client/ClientOrder/repay")
    Observable<HttpResult<PayBean>> repay(@Field("orderId") String orderId, @Field("payService") int payService);

    @GET("client/ClientOrder/list")
    Observable<HttpResult<OrderListBean>> getSendOrderList(@Query("status") String status, @Query("start") int start, @Query("limit") int limit);

    @GET("client/ClientOrder/orderTakingList")
    Observable<HttpResult<MainReceiveOrderBean>> getWaitReceiveOrderList(@Query("longitude") double longitude, @Query("latitude") double latitude, @Query("start") int start, @Query("limit") int limit);

    @FormUrlEncoded
    @POST("client/ClientOrder/orderTaking")
    Observable<HttpResult<SingleIntValue>> receiveOrder(@Field("orderId") String orderId);//接单按钮


    @GET("client/ClientOrder/realPhone")
    Observable<HttpResult<SingleStringValue>> getActionPhone(@Query("orderId") String orderId, @Query("action") int action);//获取action - 1-联系发单人 2-联系收单人

    @FormUrlEncoded
    @PUT("client/ClientOrder/deliverLockerCode")
    Observable<HttpResult<SingleIntValue>> deliverLockerCode(@Field("orderId") String orderId);//重新发送取货码

    @FormUrlEncoded
    @PUT("client/ClientOrder/deliverSuccess")
    Observable<HttpResult<SingleIntValue>> deliverSuccess(@Field("orderId") String orderId, @Field("deliverLockerCode") String deliverLockerCode);//验证取货码

    @FormUrlEncoded
    @PUT("client/ClientOrder/pickupLockerCode")
    Observable<HttpResult<SingleIntValue>> pickupLockerCode(@Field("orderId") String orderId);//重新发送收货码

    @FormUrlEncoded
    @PUT("client/ClientOrder/pickupSuccess")
    Observable<HttpResult<SingleIntValue>> pickupSuccess(@Field("orderId") String orderId, @Field("pickupLockerCode") String pickupLockerCode);//验证收货码

    @FormUrlEncoded
    @PUT("client/ClientOrder/canceled")
    Observable<HttpResult<SingleIntValue>> canceleOrder(@Field("orderId") String orderId, @Field("reason") String reason);

    @FormUrlEncoded
    @PUT("client/ClientOrder/unableToDeliver")
    Observable<HttpResult<SingleIntValue>> unableDeliver(@Field("orderId") String orderId, @Field("reason") String reason);


    @FormUrlEncoded
    @POST("client/ClientOrder/arrivalSuccess")
    Observable<HttpResult<SingleIntValue>> arrivalSuccess(@Field("orderId") String orderId);//确认送达

    @FormUrlEncoded
    @POST("client/ClientOrder/uploadDeliverImgs")
    Observable<HttpResult<SingleIntValue>> uploadDeliverImgs(@Field("orderId") String orderId, @Field("imgs") String imgs);//上传取货拍照流程


    @FormUrlEncoded
    @POST("client/ClientReview/add")
    Observable<HttpResult<ReviewBean>> addReview(@Field("orderId") String orderId, @Field("type") int type, @Field("rating") int rating, @Field("text") String text);//type - 类型（参考Review_Enum） 1=骑手对发单人的评价； 2=发单人对骑手的评价

    @GET("client/ClientReview/searchUserReviewList")
    Observable<HttpResult<ReviewM>> searchUserReviewList(@Query("userId")String userId,@Query("type")int type,@Query("start")int start,@Query("limit")int limit);

    @GET("client/ClientCustomer/my")
    Observable<HttpResult<CustomerBean>> getAuthState();


    @FormUrlEncoded
    @POST("client/ClientCertification/add")
    Observable<HttpResult<CustomerBean>> sumbitAuth(@Field("name") String name, @Field("idCard") String idCard, @Field("idCardIcon") String idCardIcon, @Field("idCardIconWithPeople") String idCardIconWithPeople);

    @Multipart
    @POST("client/ClientUpload?type=1")
    Observable<HttpResult<SingleStringValue>> upload(@Part("description") RequestBody description,
                                            @Part MultipartBody.Part file);//上传图片


    @Multipart
    @POST()
    Observable<HttpResult<UploadImagesBean>> uploadMore(@Url() String url,
                                                        @PartMap() Map<String, RequestBody> partMap);//上传多图片

    /**
     * 通过Id获取订单详情
     *
     * @param id
     * @return OrderM
     */
    @GET("client/ClientOrder/byId")
    Observable<HttpResult<OrderM>> byId(@Query("id") String id);

    /**
     * 已接单订单列表
     *
     * @param status
     * @param start
     * @param limit
     * @return
     */
    @GET("client/ClientOrder/orderTakedList")
    Observable<HttpResult<PageResultList<OrderM>>> orderTakedList(@Query("status") String status, @Query("start") String start, @Query("limit") String limit, @Query("longitude") double longitude, @Query("latitude") double latitude);

    @FormUrlEncoded
    @POST("client/ClientComplaint/add")
    Observable<HttpResult<ComplaintBean>> complaintAdd(@Field("complaintObject") int complaintObject, @Field("name") String name, @Field("mobile") String mobile, @Field("complaintReasonIds") String complaintReasonIds, @Field("otherReason") String otherReason);

    @GET("client/ClientComplaintReason/list")
    Observable<HttpResult<ComplaintReason>> getcomplaintReasonList(@Query("complaintObject") int complaintObject, @Query("start") String start, @Query("limit") String limit);

    @FormUrlEncoded
    @POST("client/ClientComments/add")
    Observable<HttpResult<Comments>> commentsAdd(@Field("type") int type, @Field("content") String content, @Field("picture") String picture);


    @GET("client/wallet/account")
    Observable<HttpResult<AccountBean>> getWalletMoney();//获取钱包余额

    @POST("client/wallet/pay")
    @FormUrlEncoded
    Observable<HttpResult<SingleStringValue>> walletPay(@Field("orderId") String orderId, @Field("password") String password);//钱包支付

    @PUT("client/wallet/recharge")
    @FormUrlEncoded
    Observable<HttpResult<PayBean>> payWallet(@Field("payService") int payService, @Field("amount") BigDecimal amount);//充值钱包

    @POST("client/WdrAcc/add")
    @FormUrlEncoded
    Observable<HttpResult<WithdrawAccountBean>> addAccount(@Field("accountType") int accountType, @Field("accountName") String accountName, @Field("accountNo") String accountNo, @Field("bankCode") String bankCode, @Field("bankName") String bankName);//钱包添加提现账户

    @POST("client/WdrAcc/update")
    @FormUrlEncoded
    Observable<HttpResult<WithdrawAccountBean>> updateAccount(@Field("id") String id, @Field("accountType") int accountType, @Field("accountName") String accountName, @Field("accountNo") String accountNo, @Field("bankCode") String bankCode, @Field("bankName") String bankName);//钱包添加提现账户

    @GET("client/WdrAcc/list")
    Observable<HttpResult<WithdrawAccountBean>> getAccountList();//钱包添加提现账户


    @HTTP(method = "DELETE", path = "client/WdrAcc/delete", hasBody = true)
    @FormUrlEncoded
    Observable<HttpResult<WithdrawAccountBean>> deleteAccount(@Field("id") String id);//删除提现账户

    @PUT("client/WdrAcc/defaults")
    @FormUrlEncoded
    Observable<HttpResult<WithdrawAccountBean>> setAccountdefaults(@Field("id") String id);//设置默认提现账户

    @PUT("client/wdrApply/add")
    @FormUrlEncoded
    Observable<HttpResult<WithdrawAccountBean>> applyWithdraw(@Field("wdrAccId") String wdrAccId, @Field("amount") BigDecimal amount, @Field("password") String password);//申请提现

    @GET("client/wallet/logs")
    Observable<HttpResult<AccountLogM>> grtAccountLogs(@Query("accountId") String accountId, @Query("start") int start, @Query("limit") int limit);

    /**
     *
     * //获取我的信息
     * @return
     */
    @GET("client/ClientCustomer/my")
    Observable<HttpResult<CustomerM>> getMe();


    @FormUrlEncoded
    @PUT("client/ClientCustomer/update")
    Observable<HttpResult<CustomerM>> update(@Field("nickName")String nickName,
                                             @Field("head") String head,@Field("sex")int sex
                                            ,@Field("address")String address
                                            ,@Field("birthday")String bri);

}

