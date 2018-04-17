package net.ed58.dlm.clients.network.http;


import android.text.TextUtils;

import net.ed58.dlm.clients.BuildConfig;
import net.ed58.dlm.clients.MyApplication;
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
import net.ed58.dlm.clients.network.MyGsonConverter;
import net.ed58.dlm.clients.network.entity.HttpResult;
import net.ed58.dlm.clients.network.subscribers.RxSubscriber;
import net.ed58.dlm.clients.sp.MyConfiguration;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 说明:
 *
 * @author: caosir
 * @time：2017/10/17
 * @param：
 * @return
 */
public class HttpMethods {

    public static String BASE_URL = BuildConfig.SERVER_URL;

//    private static String DOMIN = Constant.DEBUG?Constant.BETA_DOMIN:Constant.NORMAL_DOMIN;

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private HttpService httpService;
    private String cookie;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder;
        if (BuildConfig.DEBUG) {
            builder = new OkHttpClient.Builder().addInterceptor
                    (new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        } else {
            builder = new OkHttpClient.Builder();
        }

        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //持久化登陆的
        builder.addInterceptor(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                final StringBuffer cookieBuffer = new StringBuffer();
                Observable.from(originalResponse.headers("Set-Cookie"))
                        .map(s -> {
                            String[] cookieArray = s.split(";");
                            return cookieArray[0];
                        })
                        .subscribe(cookie1 -> cookieBuffer.append(cookie1).append(";"));
                cookie = cookieBuffer.toString();
                if (TextUtils.isEmpty(MyConfiguration.getInstance().getSession(MyApplication.getContext()))) {
                    MyConfiguration.getInstance().setSession(MyApplication.getContext(), cookie);
                }
            }
            return originalResponse;
        });

        builder.addInterceptor(chain -> {
            final Request.Builder builder1 = chain.request().newBuilder();
            Observable.just(MyConfiguration.getInstance().getSession(MyApplication.getContext()))
                    .subscribe(cookie12 -> {
                        //添加cookie
                        builder1.addHeader("Cookie", cookie12);
                    });
            return chain.proceed(builder1.build());
        });

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(MyGsonConverter.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        httpService = retrofit.create(HttpService.class);
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例

    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
            //这里可以写异常的拦截？
            return httpResult.getResult();
        }
    }


//    public void mergeMainShop(Subscriber<Object> subscriber) {
//        Observable<ShopInfoBean> infoObservable = httpService.getInfo().map(new HttpResultFunc<>());
//        Observable<ShopStatisticsBean> statisticsObservable = httpService.getStatisticsInfo().map(new HttpResultFunc<>());
//        Observable<Object> merge = Observable.merge(infoObservable, statisticsObservable);
//        toSubscribe(merge,subscriber);
//    }

    public void login(Subscriber<CustomerBean> subscriber, String mobile, String code) {
        Observable<CustomerBean> observable = httpService.login(mobile, code).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void logout(Subscriber<SingleBoolValue> subscriber) {
        Observable<SingleBoolValue> observable = httpService.logout().map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }


    public void sendLoginSmsCode(RxSubscriber<SingleIntValue> subscriber, String mobile) {

        Observable<SingleIntValue> observable = httpService.sendLoginSmsCode(mobile).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
//        observable.subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);

    }


    public void getProductType(Subscriber<ProductTypeListBean> subscriber, int type) {
        Observable<ProductTypeListBean> observable = httpService.getProductType(type).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void submitOrder(Subscriber<PreOrderBean> subscriber, Map<String, Object> map) {
        Observable<PreOrderBean> observable = httpService.submitOrder(map).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void getCurrentTime(Subscriber<SingleStringValue> subscriber) {
        Observable<SingleStringValue> observable = httpService.getCurrentTime().map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void getHostoryAddressList(RxSubscriber<Address> subscriber, String start, String limit) {
        Observable<Address> observable = httpService.getHostoryAddressList(start, limit).map(new HttpResultFunc<Address>());
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getInsuredList(RxSubscriber<Insured> subscriber, String start, String limit) {
        Observable<Insured> observable = httpService.getInsuredList(start, limit).map(new HttpResultFunc<Insured>());
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void repay(Subscriber<PayBean> subscriber, String orderId, int payService) {
        Observable<PayBean> observable = httpService.repay(orderId, payService).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void getSendOrderList(Subscriber<OrderListBean> subscriber, String status, Integer start) {
        Observable<OrderListBean> observable = httpService.getSendOrderList(status, start, 5).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void getWaitReceiveOrderList(Subscriber<MainReceiveOrderBean> subscriber, double longitude, double latitude, Integer start) {
        Observable<MainReceiveOrderBean> observable = httpService.getWaitReceiveOrderList(longitude, latitude, start, 5).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void canceleOrder(Subscriber<SingleIntValue> subscriber, String orderId, String reason) {
        Observable<SingleIntValue> observable = httpService.canceleOrder(orderId, reason).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void unableDeliver(Subscriber<SingleIntValue> subscriber, String orderId, String reason) {
        Observable<SingleIntValue> observable = httpService.unableDeliver(orderId, reason).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void arrivalSuccess(Subscriber<SingleIntValue> subscriber, String orderId) {
        Observable<SingleIntValue> observable = httpService.arrivalSuccess(orderId).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void uploadDeliverImgs(Subscriber<SingleIntValue> subscriber, String orderId, String imgs) {
        Observable<SingleIntValue> observable = httpService.uploadDeliverImgs(orderId, imgs).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void addReview(Subscriber<ReviewBean> subscriber, String orderId, int type, int rating, String text) {
        Observable<ReviewBean> observable = httpService.addReview(orderId, type, rating, text).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void searchUserReviewList(Subscriber<ReviewM> subscriber, String userId, int type, int start) {
        Observable<ReviewM> observable = httpService.searchUserReviewList(userId, type, start, 5).map(new HttpResultFunc<ReviewM>());

        toSubscribe(observable, subscriber);
    }

    public void receiveOrder(Subscriber<SingleIntValue> subscriber, String orderId) {
        Observable<SingleIntValue> observable = httpService.receiveOrder(orderId).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void getActionPhone(Subscriber<SingleStringValue> subscriber, String orderId, int action) {
        Observable<SingleStringValue> observable = httpService.getActionPhone(orderId, action).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void deliverLockerCode(Subscriber<SingleIntValue> subscriber, String orderId) {
        Observable<SingleIntValue> observable = httpService.deliverLockerCode(orderId).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void deliverSuccess(Subscriber<SingleIntValue> subscriber, String orderId, String deliverLockerCode) {
        Observable<SingleIntValue> observable = httpService.deliverSuccess(orderId, deliverLockerCode).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void pickupLockerCode(Subscriber<SingleIntValue> subscriber, String orderId) {
        Observable<SingleIntValue> observable = httpService.pickupLockerCode(orderId).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void pickupSuccess(Subscriber<SingleIntValue> subscriber, String orderId, String deliverLockerCode) {
        Observable<SingleIntValue> observable = httpService.pickupSuccess(orderId, deliverLockerCode).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }


    public void getAuthState(Subscriber<CustomerBean> subscriber) {
        Observable<CustomerBean> observable = httpService.getAuthState().map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public void sumbitAuth(Subscriber<CustomerBean> subscriber, String name, String idCard, String idCardIcon, String idCardIconWithPeople) {
        Observable<CustomerBean> observable = httpService.sumbitAuth(name, idCard, idCardIcon, idCardIconWithPeople).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    public Observable<SingleStringValue> upload(Subscriber<SingleStringValue> subscriber, String fileName, File file) {
        //文件为空发送一个空的发送源
        if(TextUtils.isEmpty(fileName)){
            SingleStringValue singleStringValue = new SingleStringValue();
            singleStringValue.setValue(fileName);
            Observable<SingleStringValue> observable = Observable.just(singleStringValue);
            return observable;
        } else {
            fileName = "file";
        }

        // 创建 RequestBody，用于封装 请求RequestBody
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(fileName, file.getName(), requestFile);

        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fileName);


        Observable<SingleStringValue> observable = httpService.upload(description, body).map(new HttpResultFunc<>());
        if (subscriber != null) {
            toSubscribe(observable, subscriber);
            return null;
        } else {
            return observable;
        }
    }

    public void uploadMore(Subscriber<UploadImagesBean> subscriber, Map<String, RequestBody> params) {

        Observable observable = httpService.uploadMore("client/ClientUpload?type=1&multi=true", params).map(new HttpResultFunc<>());

        toSubscribe(observable, subscriber);
    }

    /**
     * 通过ID 获取订单详情
     *
     * @param subscriber 订阅者的操作
     * @param id         订单Id
     */
    public void orderMById(Subscriber<OrderM> subscriber, String id) {
        Observable observable = httpService.byId(id).map(new HttpResultFunc<OrderM>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 已接单订单列表
     *
     * @param subscriber
     * @param status     400-已接单(待取货),500-已取货(配送中)
     * @param start
     * @param limit
     */
    public void orderTakedList(Subscriber<PageResultList<OrderM>> subscriber, String status, String start, String limit, double longitude, double latitude) {
        Observable observable = httpService.orderTakedList(status, start, limit, longitude, latitude).map(new HttpResultFunc<PageResultList<OrderM>>());
        toSubscribe(observable, subscriber);
    }

    public void complaintAdd(Subscriber<ComplaintBean> subscriber, int complaintObject, String name, String mobile, String complaintReasonIds, String otherReason) {
        Observable observable = httpService.complaintAdd(complaintObject, name, mobile, complaintReasonIds, otherReason).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void getcomplaintReasonList(Subscriber<ComplaintReason> subscriber, int complaintObject, String start, String limit) {
        Observable observable = httpService.getcomplaintReasonList(complaintObject, start, limit).map(new HttpResultFunc<ComplaintReason>());
        toSubscribe(observable, subscriber);
    }

    public void submitComments(Subscriber<Comments> subscriber, int type, String connent, String picture) {
        Observable observable = httpService.commentsAdd(type, connent, picture).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void getWalletMoney(Subscriber<AccountBean> subscriber) {
        Observable observable = httpService.getWalletMoney().map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void walletPay(Subscriber<SingleStringValue> subscriber, String orderId, String password) {
        Observable observable = httpService.walletPay(orderId, password).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void payWallet(Subscriber<PayBean> subscriber, int payService, BigDecimal amount) {
        Observable observable = httpService.payWallet(payService, amount).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }


    public void addAccount(Subscriber<WithdrawAccountBean> subscriber, int accountType, String accountName, String accountNo, String bankCode, String bankName) {
        Observable observable = httpService.addAccount(accountType, accountName, accountNo, bankCode, bankName).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void updateAccount(Subscriber<WithdrawAccountBean> subscriber, String id, int accountType, String accountName, String accountNo, String bankCode, String bankName) {
        Observable observable = httpService.updateAccount(id, accountType, accountName, accountNo, bankCode, bankName).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void deleteAccount(Subscriber<WithdrawAccountBean> subscriber, String id) {
        Observable observable = httpService.deleteAccount(id).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void setAccountdefaults(Subscriber<WithdrawAccountBean> subscriber, String id) {
        Observable observable = httpService.setAccountdefaults(id).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }


    public void getAccountList(Subscriber<WithdrawAccountBean> subscriber) {
        Observable observable = httpService.getAccountList().map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void applyWithdraw(Subscriber<WithdrawAccountBean> subscriber, String wdrAccId, BigDecimal amount, String password) {
        Observable observable = httpService.applyWithdraw(wdrAccId, amount, password).map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public void getAccountLogs(Subscriber<AccountLogM> subscriber, String accountId, int start) {
        Observable observable = httpService.grtAccountLogs(accountId, start, 8).map(new HttpResultFunc<AccountLogM>());
        toSubscribe(observable, subscriber);
    }


    /**
     * 获取个人信息
     *
     * @param subscriber
     */
    public void getMe(Subscriber<CustomerM> subscriber) {
        Observable observable = httpService.getMe().map(new HttpResultFunc<>());
        toSubscribe(observable, subscriber);
    }

    public Observable<CustomerM> update(String nickName,
                                        String head, int sex
            , String address) {
        Observable observable = httpService.update(nickName, head, sex, address, "").map(new HttpResultFunc<>());
        return observable;
    }

}

