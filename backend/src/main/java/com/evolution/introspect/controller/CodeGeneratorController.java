package com.evolution.introspect.controller;

import com.evolution.introspect.service.ICodeGenerateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zzz
 * @date 2025/7/24-19:07
 * @desc 代码生成器控制器
 */
@RestController
@RequestMapping("/code-generator")
public class CodeGeneratorController {

    @Resource
    ICodeGenerateService codeGenerateService;
    // todo 接口执行建表语句

    // todo 接口-分页查询数据库中的的表名

    // todo 接口-修改表结构

    // todo 接口-生成某张或者某些表的代码
    @GetMapping
    @Operation(description = "生成某张或者某些表的代码")
    public void generate(@RequestParam("tableNames") List<String> tableNames) {
        codeGenerateService.codeGenerate(tableNames);
    }
}
