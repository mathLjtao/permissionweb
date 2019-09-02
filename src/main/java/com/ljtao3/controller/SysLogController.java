package com.ljtao3.controller;

import com.ljtao3.beans.PageQuery;
import com.ljtao3.beans.PageResult;
import com.ljtao3.common.JsonData;
import com.ljtao3.model.SysLogWithBLOBs;
import com.ljtao3.param.SearchLogParam;
import com.ljtao3.service.SysLogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/log")
public class SysLogController {
    @Resource
    private SysLogService sysLogService;

    @RequestMapping("/log.page")
    public ModelAndView page(){
        return new ModelAndView("log");
    }
    /*
    根据查询条件，查出符合条件的log记录
     */
    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData logPage(SearchLogParam param , PageQuery pageQuery){
        PageResult<SysLogWithBLOBs> result= sysLogService.getByCondWithPage(param,pageQuery);
        return JsonData.success(result);
    }
    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(@RequestParam("id") Integer id){
        sysLogService.recover(id);
        return JsonData.success();
    }
}
