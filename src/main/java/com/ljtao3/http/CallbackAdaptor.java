package com.ljtao3.http;

import com.ljtao3.http.ext.AuthSSLInitializationError;
import lombok.extern.slf4j.Slf4j;

/**
 * 简化HttpCallback接口的实现复杂度
 * Created by jimin on 16/03/10.
 */
@Slf4j
public class CallbackAdaptor implements HttpCallback {

    @Override
    public void onSuccess(ResponseWrapper wrapper) {
    }

    @Override
    public void onFailure(Throwable t) {
        throw new RuntimeException(t.getMessage(), t);
    }

    @Override
    public void onAuthority(AuthSSLInitializationError t) {
        log.warn("认证失败", t);
    }

}
