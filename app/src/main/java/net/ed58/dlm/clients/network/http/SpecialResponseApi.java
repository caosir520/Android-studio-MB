package net.ed58.dlm.clients.network.http;


import net.ed58.dlm.clients.BuildConfig;
import net.ed58.dlm.clients.MyApplication;
import net.ed58.dlm.clients.sp.SPKey;
import net.ed58.dlm.clients.sp.SettingUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sunpeng on 17/3/22.
 * 给特殊情况的接口使用，服务端懒的重写，我就用了这个
 */

public class SpecialResponseApi {

    public static String BASE_URL = BuildConfig.SERVER_URL;

//    private static String DOMIN = Constant.DEBUG?Constant.BETA_DOMIN:Constant.NORMAL_DOMIN;

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private HttpService httpService;
    private String cookie;

    //构造方法私有
    private SpecialResponseApi() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder;
        if (BuildConfig.DEBUG) {
            builder = new OkHttpClient.Builder().addInterceptor
                    (new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        } else {
            builder = new OkHttpClient.Builder();
        }

        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);


        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request.Builder builder = chain.request().newBuilder();
                Observable.just(SettingUtils.getSharedPreferences(MyApplication.getContext(), SPKey.KEY_ACCESS_COOKIE, ""))
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String cookie) {
                                //添加cookie
                                builder.addHeader("Cookie", cookie);
                            }
                        });
                return chain.proceed(builder.build());
            }
        });

        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        httpService = retrofit.create(HttpService.class);
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final SpecialResponseApi INSTANCE = new SpecialResponseApi();
    }

    //获取单例
    public static SpecialResponseApi getInstance() {
        return SingletonHolder.INSTANCE;
    }

//
//    public void getHtmlImage(Func1<ResponseBody,Boolean> func1,Subscriber<Boolean> subscriber, String url) {
//        Observable<ResponseBody> observable = httpService.getHtmlImage(url);
//
//        observable.unsubscribeOn(Schedulers.io())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(func1).subscribe(subscriber);
//    }


    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

}
