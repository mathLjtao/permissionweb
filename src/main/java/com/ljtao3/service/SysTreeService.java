package com.ljtao3.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.ljtao3.dao.SysDeptMapper;
import com.ljtao3.dto.DeptLevelDto;
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
}
