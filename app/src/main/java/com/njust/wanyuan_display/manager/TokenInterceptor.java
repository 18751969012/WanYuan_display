package com.njust.wanyuan_display.manager;


import android.content.Context;
import android.widget.Toast;

import com.njust.wanyuan_display.action.InitMachineAction;
import com.njust.wanyuan_display.config.Resources;
import com.njust.wanyuan_display.db.DBManager;
import com.njust.wanyuan_display.util.ToastManager;

import org.apache.log4j.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class TokenInterceptor implements Interceptor {

    private final Logger log = Logger.getLogger(TokenInterceptor.class);

    private Context context;

    public TokenInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        log.info("response.code=" + response.code());
        boolean success = false;
        int rePostNum = 0;
        if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
            log.info("静默自动刷新csrf令牌,然后重新请求数据");
            while(!success && rePostNum<5 ){
                //同步请求方式，获取最新的csrf令牌
                String csrf = OkHttp3Manager.getInstance(context).get_token(context, Resources.get_token);
                new BindingManager().setCsrf(context, csrf);
                success = new InitMachineAction().initMachine(context, DBManager.userName(context), DBManager.userPassword(context));
//                success = true;
                if(success){
                    //使用新的csrf令牌，创建新的请求
                    Request newRequest = chain.request()
                            .newBuilder()
                            .build();
                    //重新请求
                    return chain.proceed(newRequest);
                }else{
                    rePostNum++;
                    if(rePostNum >= 5){
                        //需要退出到绑定页面
                        ToastManager.getInstance().showToast(context, "重新自动登录失败，请联系客服人员，重新绑定登录", Toast.LENGTH_LONG);
                    }
                }
                }
        }
        return response;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(Response response) {
        if (response.code() == 404) {
            return true;
        }
        return false;
    }


}