package com.ljtao3.util;

import com.ljtao3.common.SpringHelper;
import com.ljtao3.config.GlobalConfig;
import com.ljtao3.config.GlobalConfigKey;
import com.ljtao3.dto.CookieUser;
import com.ljtao3.dto.LoginUser;
import com.ljtao3.enums.Status;
import com.ljtao3.model.SysUser;
import com.ljtao3.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class LoginUtil {
    /**/
    private final static String DEFAULT = "@6CFB18E2#iLpYs8Tb";

    private final static String DEFAULT_USER_COOKIE = "_U";

    private final static String USER_NAME_COOKIE = "_UN";

    private final static String USER_MAIL_COOKIE = "_UM";

    private final static String PREFIX = "9c0Mk$%S9mD&Yu";

    private final static String SUFFIX = "uIml&8k#pI92*Qr";

    private final static int DEFAULT_EXPIRE_SECONDS = 1800;

    public static void saveUserToCookie(HttpServletRequest request, HttpServletResponse response, SysUser sysUser) {
        if (sysUser == null) {
            return;
        }
        try {
            String userCookie = generateUserCookie(request, sysUser);
            //将上面生成的编码保存在cookie中
            CookieUtil.setCookie(request, response, getUserNameCookie(), userCookie, getDefaultExpireSeconds());
            //将用户名保存在cookie中
            CookieUtil.setCookie(request, response, USER_NAME_COOKIE, sysUser.getUsername(), getDefaultExpireSeconds());
            //将邮箱名保存在cookie中
            CookieUtil.setCookie(request, response, USER_MAIL_COOKIE, sysUser.getMail(), getDefaultExpireSeconds());
        } catch (Throwable t) {
            log.error("user login succeed, save cookie exception, user: {}", JsonMapper.obj2String(sysUser), t);
        }
    }

    public static LoginUser getUserFromCookie(HttpServletRequest request, HttpServletResponse response) {
        String userCookie = "";
        try {
            Cookie cookie = CookieUtil.getCookie(request, getUserNameCookie());
            if (cookie == null || StringUtils.isEmpty(cookie.getValue())) {
                return LoginUser.fail("未获取到登录的用户信息,请登录");
            }
            userCookie = cookie.getValue();
            if (!userCookie.startsWith(PREFIX) || !userCookie.endsWith(SUFFIX)) {
                return LoginUser.fail("用户信息校验不通过,请重新登录");
            }
            // 先去掉前缀和后缀, 再反转
            userCookie = StringUtils.reverse(userCookie.replace(PREFIX, "").replace(SUFFIX, ""));
            // 使用=替换默认的字符串, base64解密
            String decodeBase64 = Base64Util.decodeStr(userCookie.replaceAll(DEFAULT, "="));
            // json串反转为CookieUser对象
            CookieUser cookieUser = JsonMapper.string2ObjByClass(decodeBase64, CookieUser.class);
            if (cookieUser == null) {
                return LoginUser.fail("获取登录的用户信息出现问题,请重新登录");
            }

            // 校验是否过期
            long during = System.currentTimeMillis() - cookieUser.getLastLogin();
            if (during / 1000 > getDefaultExpireSeconds()) {
                return LoginUser.fail("当前用户登录信息已过期,请重新登录");
            }
            //在cookie获取用户的信息完毕，开始查数据库，查看有没有该用户
            SysUserService sysUserService = SpringHelper.popBean(SysUserService.class);
            SysUser sysUser = sysUserService.findById(cookieUser.getUserId());
            if (sysUser == null) {
                return LoginUser.fail("当前用户未在系统中查询到,请重新登录");
            }
            if (sysUser.getStatus() != Status.AVAILABLE.getCode()) {
                return LoginUser.fail("当前用户状态无效,请联系管理员");
            }
            if (!sysUser.getMail().equals(cookieUser.getUsername())) {
                return LoginUser.fail("当前用户名和系统中记录的用户名不一致,请重新登录");
            }

            String ip = IpUtil.getRemoteIp(request);
            String mac = IpUtil.getMACAddress(ip).replaceAll("-", "");
            if (!mac.equals(cookieUser.getMac())) {
                log.warn("检测出用户mac地址和cookie中的记录的mac不一致,可能是在拼cookie, username:{}", sysUser.getUsername());
                //return LoginUser.fail("当前用户登录信息和登录时的设备不一致,请重新登录");
            }
            if (!ip.equals(cookieUser.getIp())) {
                log.error("检测出用户ip地址和cookie中的记录的ip不一致,可能是在拼cookie, username:{}", sysUser.getUsername());
            }

            saveUserToCookie(request, response, sysUser);
            return LoginUser.success(sysUser);
        } catch (Throwable t) {
            log.error("handle user cookie error, cookie: {}", userCookie, t);
            return LoginUser.fail("处理用户信息出错,请重新登录");
        }
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.setCookie(request, response, getUserNameCookie(), "", 0);
    }
    //获取失效时间，单位是seconds
    private static int getDefaultExpireSeconds() {
        return GlobalConfig.getIntValue(GlobalConfigKey.COOKIE_EXPIRE_SECONDS, DEFAULT_EXPIRE_SECONDS);
    }

    public static String getUserNameCookie() {
        return GlobalConfig.getStringValue(GlobalConfigKey.COOKIE_USER_FLAG, DEFAULT_USER_COOKIE);
    }
    //对信息进行处理，加密
    private static String generateUserCookie(HttpServletRequest request, SysUser sysUser) {
        String ip = IpUtil.getRemoteIp(request);
        String mac = IpUtil.getMACAddress(ip).replaceAll("-", "");
        // 设置cookieUser类
        CookieUser cookieUser = CookieUser.builder().userId(sysUser.getId()).username(sysUser.getMail()).ip(ip).mac(mac).lastLogin(System.currentTimeMillis())
                .build();
        // 得到cookieUser类对应的json串,base64加密后,并将=使用默认字符替换
        String encodeBase = Base64Util.encode(JsonMapper.obj2String(cookieUser).getBytes()).replaceAll("=", DEFAULT);
        // 反转
        encodeBase = StringUtils.reverse(encodeBase);
        // 补充上前缀和后缀
        encodeBase = PREFIX + encodeBase + SUFFIX;
        return encodeBase;
    }

}
