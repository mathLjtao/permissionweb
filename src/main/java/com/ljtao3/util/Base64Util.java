package com.ljtao3.util;

import org.apache.commons.codec.binary.Base64;

/**
 * 统一项目中base64算法的使用。可避免项目中引用不同三方库去实现相同代码
 * Created by jimin on 16/2/10
 */
public class Base64Util {

    /**
     * encode for
     * byte - > byte using base64
     *
     * @param binaryData
     * @return
     */
    public static byte[] encodeByte(final byte[] binaryData) {
        return Base64.encodeBase64(binaryData, false);
    }

    /**
     * encode for
     * byte - > string using base64
     *
     * @param binaryData
     * @return
     */
    //字符串加密
    public static String encode(final byte[] binaryData) {
        return Base64.encodeBase64String(binaryData);
    }

    /**
     * decode for
     * string -> byte using base64
     *
     * @param base64String
     * @return
     */
    public static byte[] decode(final String base64String) {
        return new Base64().decode(base64String);
    }

    /**
     * decode for
     * byte -> byte using base64
     *
     * @param base64Data
     * @return
     */
    public static byte[] decode(final byte[] base64Data) {
        return new Base64().decode(base64Data);
    }
    /*
    字符串解密
     */
    public static String decodeStr(final String base64String) {
        return new String(decode(base64String));
    }
}