package com.ljtao3.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.ljtao3.dao.SysAclModuleMapper;
import com.ljtao3.dao.SysDeptMapper;
import com.ljtao3.dto.AclModuleLevelDto;
import com.ljtao3.dto.DeptLevelDto;
import com.ljtao3.model.SysAclModule;
import com.ljtao3.model.SysDept;
import com.ljtao3.util.LevelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SysTreeService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    public  List<DeptLevelDto> deptTree(){
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList=new ArrayList<>();
        for(SysDept dept:deptList){
            DeptLevelDto dto= DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }
    public List<DeptLevelDto> deptListToTree (List<DeptLevelDto> deptLevelDtoList){
        if(CollectionUtils.isEmpty(deptLevelDtoList)){
            return Lists.newArrayList();
        }
        // level -> [dept1, dept2, ...] Map<String, List<Object>>
        //key 为String类型，value为DeptLevelDto集合，value中的子集不重复
        Multimap<String,DeptLevelDto>  levelDeptMap=ArrayListMultimap.create();
        List<DeptLevelDto> rootList=Lists.newArrayList();
        for (DeptLevelDto dto:deptLevelDtoList){
            levelDeptMap.put(dto.getLevel(),dto);
            if(LevelUtils.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按照seq从小到大排序
        Collections.sort(rootList,deptSepComparator);
        //递归生成树
        transformDeptTree(rootList,LevelUtils.ROOT,levelDeptMap);
        return rootList;
    }

    private void transformDeptTree(List<DeptLevelDto> rootList, String level, Multimap<String, DeptLevelDto> levelDeptMap) {
        for(int i=0;i<rootList.size();i++){
            //遍历每层的元素
            DeptLevelDto deptLevelDto = rootList.get(i);
            //处理当层级的数据
            String nextLevel = LevelUtils.calculateLevel(level, deptLevelDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto> )levelDeptMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList,deptSepComparator);
                //设置下一层部门
                deptLevelDto.setDeptLevelDtoList(tempDeptList);
                //进入下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
            }

        }
    }

    public Comparator<DeptLevelDto> deptSepComparator =new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    /*
    权限模块的列表树设置
     */
    public  List<AclModuleLevelDto> aclModuleTree(){
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList=new ArrayList<>();
        for(SysAclModule aclModule:aclModuleList){
            AclModuleLevelDto dto= AclModuleLevelDto.adapt(aclModule);
            dtoList.add(dto);
        }
        return aclModuleListToTree(dtoList);
    }
    public List<AclModuleLevelDto> aclModuleListToTree (List<AclModuleLevelDto> aclModuleLevelDtoList){
        if(CollectionUtils.isEmpty(aclModuleLevelDtoList)){
            return Lists.newArrayList();
        }
        // level -> [dept1, dept2, ...] Map<String, List<Object>>
        //key 为String类型，value为DeptLevelDto集合，value中的子集不重复
        Multimap<String,AclModuleLevelDto>  levelAclModuleMap=ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList=Lists.newArrayList();
        for (AclModuleLevelDto dto:aclModuleLevelDtoList){
            levelAclModuleMap.put(dto.getLevel(),dto);
            if(LevelUtils.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按照seq从小到大排序
        Collections.sort(rootList,aclModuleSepComparator);
        //递归生成树
        transformAclModuleTree(rootList,LevelUtils.ROOT,levelAclModuleMap);
        return rootList;
    }

    private void transformAclModuleTree(List<AclModuleLevelDto> rootList, String level, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for(int i=0;i<rootList.size();i++){
            //遍历每层的元素
            AclModuleLevelDto aclModuleLevelDto = rootList.get(i);
            //处理当层级的数据
            String nextLevel = LevelUtils.calculateLevel(level, aclModuleLevelDto.getId());
            //处理下一层
            List<AclModuleLevelDto> tempAclModuleList = (List<AclModuleLevelDto> )levelAclModuleMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempAclModuleList)){
                //排序
                Collections.sort(tempAclModuleList,aclModuleSepComparator);
                //设置下一层部门
                aclModuleLevelDto.setAclModuleList(tempAclModuleList);
                //进入下一层处理
                transformAclModuleTree(tempAclModuleList,nextLevel,levelAclModuleMap);
            }

        }
    }

    public Comparator<AclModuleLevelDto> aclModuleSepComparator =new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };
}
