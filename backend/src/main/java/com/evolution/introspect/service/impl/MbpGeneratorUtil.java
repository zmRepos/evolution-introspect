package com.evolution.introspect.service.impl;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.evolution.introspect.config.MineCodeGenerateConfig;
import com.evolution.introspect.service.ICodeGenerateService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MbpGeneratorUtil implements ICodeGenerateService {

    @Resource
    MineCodeGenerateConfig config;

    @Override
    public void codeGenerate(String tableName) {
        FastAutoGenerator
                .create(config.getDbUrl(),config.getDbUserName(),config.getDbPwd())
                .globalConfig(gc -> {
                    gc.disableOpenDir();
                    gc.outputDir(config.getTargetPath());
                    gc.author(config.getAuthor());
                    //gc.enableSwagger();
                    gc.enableSpringdoc();
                    //gc.enableKotlin();
                    gc.dateType(DateType.TIME_PACK);
                    gc.commentDate(config.getCommentDateFormat());
                })
                .dataSourceConfig(e->{
                    e.keyWordsHandler(new MySqlKeyWordsHandler());
                })
                .packageConfig(e -> {
                    // 将_转化成小驼峰写法
                    e.parent(config.getPackagePath());
                    e.moduleName("%s");
                    e.entity("domain.entity");
                    e.service("service");
                    e.serviceImpl("service.impl");
                    e.controller("controller");
                    e.mapper("mapper");
                    e.xml("mapper");
                    Map<OutputFile, String> pathMap = new HashMap<>();
                    pathMap.put(OutputFile.xml, config.getTargetPath()+ File.pathSeparator + "mapper");
                    e.pathInfo(pathMap);
                })
                .strategyConfig(e -> {
                        e.addInclude(tableName)   // 包含的表（生成这些表的代码）
                        .addTablePrefix(config.getTablePrefix())             // 表前缀过滤（生成实体类时去掉前缀）

                        .entityBuilder()                     // 实体类策略
                        .javaTemplate("/templates/domain/entity/entity.java.vm")
                        .enableLombok()                // 启用Lombok
                        .enableTableFieldAnnotation()   // 生成字段注解
                        .formatFileName("%s")     // 实体类命名格式
                        .columnNaming(NamingStrategy.underline_to_camel)
                        .naming(NamingStrategy.underline_to_camel)
                        .enableFileOverride()

                        .mapperBuilder()                     // Mapper策略
                        .formatMapperFileName("%sMapper") // Mapper接口命名
                        .formatXmlFileName("%sMapper")  // XML文件命名
                        .mapperTemplate("/templates/mapper/mapper.java.vm")
                        .mapperXmlTemplate("/templates/mapper/mapper.xml.vm")
                        .enableFileOverride()

                        .serviceBuilder()                    // Service策略
                        .formatServiceFileName("I%sService") // Service接口命名
                        .formatServiceImplFileName("%sServiceImpl") // Service实现类命名
                        .serviceTemplate("/templates/service/service.java.vm")
                        .serviceTemplate("/templates/service/impl/serviceImpl.java.vm")
                        .enableFileOverride()

                        .controllerBuilder()                 // Controller策略
                        .enableRestStyle()              // 生成@RestController
                        .enableFileOverride()
                        .formatFileName("%sController")// Controller文件命名
                        .template("templates/controller/controller.java.vm");
                })
                .injectionConfig(consumer->{
                    Map<String, Object> customMap = new HashMap<>();
                    customMap.put("dto", config.getPackagePath() + ".domain.dto");
                    customMap.put("bo", config.getPackagePath() + ".domain.bo");
                    customMap.put("vo", config.getPackagePath() + ".domain.vo");
                    customMap.put("convert", config.getPackagePath() + ".domain.convert");
                    consumer.customMap(customMap);
                    // DTO
                    List<CustomFile> customFiles = new ArrayList<>();
                    customFiles.add(new CustomFile.Builder().packageName("dto").fileName("DTO.java")
                            .templatePath("/templates/domain/dto/DTO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("vo").fileName("VO.java")
                            .templatePath("/templates/domain/vo/VO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("bo").fileName("BO.java")
                            .templatePath("/templates/domain/bo/BO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("convert").fileName("Convert.java")
                            .templatePath("/templates/domain/convert/Convert.java.vm").enableFileOverride().build());
                    consumer.customFile(customFiles);
                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }

    /**
     * 将下划线命名转换为小驼峰命名
     * @param underscoreName 下划线命名的字符串
     * @return 小驼峰命名的字符串
     */
    public static String underlineToCamel(String underscoreName) {
        if (underscoreName == null || underscoreName.isEmpty()) {
            return underscoreName;
        }

        StringBuilder result = new StringBuilder();
        String[] parts = underscoreName.split("_");

        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                // 第一个单词全小写
                result.append(parts[i].toLowerCase());
            } else {
                // 后续单词首字母大写
                if (!parts[i].isEmpty()) {
                    result.append(Character.toUpperCase(parts[i].charAt(0)))
                            .append(parts[i].substring(1).toLowerCase());
                }
            }
        }

        return result.toString();
    }
}
