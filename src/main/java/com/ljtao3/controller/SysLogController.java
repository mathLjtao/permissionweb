package com.ljtao3.controller;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.JsonData;
import com.ljtao3.model.SysLog;
import com.ljtao3.model.SysLogWithBLOBs;
import com.ljtao3.param.LogParam;
import com.ljtao3.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/log")
public class SysLogController {
    @Resource
    private SysLogService sysLogService;
    @RequestMapping("/log.page")
    public ModelAndView page(){
        return new ModelAndView("log");
    }
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData logPage(LogParam param ,PageQuery pageQuery){
        PageResult<SysLogWithBLOBs> result= sysLogService.getByCondWithPage(param,pageQuery);
        return JsonData.success(result);
    }
}
