package com.ljtao3.http;

import com.ljtao3.http.ext.AuthSSLInitializationError;
import com.google.common.util.concurrent.FutureCallback;

/**
 * http回调接口
 * Created by jimin on 16/03/10.
 */
public interface HttpCallback extends FutureCallback<ResponseWrapper> {

    /**
     * 正确返回的时候将调用此方法
     *
     * @param wrapper ResponseWrapper
     */
    void onSuccess(ResponseWrapper wrapper);

    /**
     * 产生异常的时候调用此方法
     *
     * @param t Throwable
     */
    void onFailure(Throwable t);

    /**
     * https 认证失败调用
     *
     * @param t AuthSSLInitializationError
     */
    void onAuthority(AuthSSLInitializationError t);
}
