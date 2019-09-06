package com.ljtao3.convert;

import com.ljtao3.common.MyRequestHolder;
import com.ljtao3.exception.ParamException;
import com.ljtao3.model.SysBase;
import com.ljtao3.model.SysUser;
import com.ljtao3.util.BeanValidator;
import com.ljtao3.util.IpUtil;
import org.apache.commons.collections.MapUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseConvert {
    public static SysBase of() {
        SysUser sysUser = MyRequestHolder.getCurrentUser();
        HttpServletRequest request = MyRequestHolder.getCurrentRequest();
        return SysBase.builder().operator(sysUser.getUsername()).operateIp(IpUtil.getRemoteIp(request)).build();
    }

    public static void checkPara(Object para) throws ParamException {
        //Map<String, String> errors = BeanValidator.validateForObjects(para);
        Map<String, String> errors= new HashMap<>();
        if (MapUtils.isNotEmpty(errors)) {
            throw new ParamException(errors.toString());
        }
    }
}
