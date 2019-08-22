package com.ljtao3.controller;

import com.ljtao3.common.JsonData;
import com.ljtao3.dto.DeptLevelDto;
import com.ljtao3.model.SysDept;
import com.ljtao3.param.DeptParam;
import com.ljtao3.service.SysDeptService;
import com.ljtao3.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {
    @Resource
    private SysDeptService sysDeptService;
    @Resource
    private SysTreeService sysTreeService;
    @RequestMapping("/dept.page")
    public ModelAndView page(){
        return new ModelAndView("dept");
    }
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(DeptParam param){
        sysDeptService.save(param);
        return  JsonData.success();
    }
    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<DeptLevelDto> dtoList = sysTreeService.deptTree();
        return JsonData.success(dtoList);
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(DeptParam param){
        sysDeptService.update(param);
        return  JsonData.success();
    }
}
