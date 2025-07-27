package com.evolution.introspect.service;

import org.springframework.util.CollectionUtils;

import java.util.List;

public interface ICodeGenerateService {
    /**
     * 根据表名列表生成代码
     * @param tableNames 表名列表
     */
    default void codeGenerate(List<String> tableNames){
        if (!CollectionUtils.isEmpty(tableNames)) {
            for (String tableName : tableNames) {
                codeGenerate(tableName);
            }
        }
    }
    
    /**
     * 根据单个表名生成代码
     * @param tableName 表名
     */
    void codeGenerate(String tableName);
}
