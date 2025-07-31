package com.evolution.introspect.util;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.evolution.introspect.model.MbpGeneratorConfig;
import com.evolution.introspect.pojo.BaseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

/**
 * @author ZhuMing
 * @date 2024/5/22
 **/
public class MbpGeneratorUtil {

    private static final String OUTPUT_PATH = System.getProperty("user.dir");

    private MbpGeneratorUtil() {
    }


    public static void generate(MbpGeneratorConfig config) {
        //1、配置数据源
        FastAutoGenerator.create(config.getJdbcUrl(), config.getUsername(), config.getPassword())
                //2、全局配置
                .globalConfig(builder -> {
                    builder.disableOpenDir() // 禁止打开输出目录 默认 true
                            .outputDir(OUTPUT_PATH + "/backend/src/main/java")   // 设置输出路径：项目的 java 目录下
                            .author(config.getAuthor()) // 设置作者名p
                            .enableSpringdoc()   // 开启 Spring doc 模式
                            .dateType(DateType.TIME_PACK)   // 定义生成的实体类中日期的类型 TIME_PACK=LocalDateTime;ONLY_DATE=Date;
                            .commentDate("yyyy/MM/dd"); // 注释日期 默认值 yyyy-MM-dd
                })
                //3、包配置
                .packageConfig(builder -> {
                    builder.parent(config.getParent()) // 父包名 默认值 com.baomidou
                            .moduleName(config.getModuleName())   // 父包模块名 默认值 无
                            .entity("domain.entity")   // Entity 包名 默认值 entity
                            .service("service") //Service 包名 默认值 service
                            .serviceImpl("service.impl") // Service Impl 包名 默认值:service.impl
                            .mapper("mapper")   // Mapper 包名 默认值 mapper
                            .xml("mapper")  // Mapper XML 包名 默认值 mapper.xml
                            .controller("controller") // Controller 包名 默认值 controller
                            .pathInfo(Collections.singletonMap(OutputFile.xml, OUTPUT_PATH + "/src/main/java/cn/nju/" + config.getModuleName() + "/mapper"));    //配置 mapper.xml 路径信息：项目的 resources 目录下
                })
                //4、模版配置
                .templateConfig(builder -> {
                    builder.entity("/templates/entity.java")
                            .service("/templates/service.java")
                            .serviceImpl("/templates/serviceImpl.java")
                            .mapper("/templates/mapper.java")
                            .xml("/templates/mapper.xml")
                            .controller("/templates/controller.java");
                })
                //5、策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(config.getTableNames()) // 设置需要生成的数据表名
                            .addTablePrefix(config.getTablePrefix().split(",")) // 设置过滤表前缀
                            //5.1、实体类策略配置
                            .entityBuilder()
                            .enableFileOverride() // 覆盖entity
                            .superClass(BaseDO.class)
                            //.disableSerialVersionUID()  // 禁用生成 serialVersionUID 默认值 true
                            .enableLombok() // 开启 Lombok 默认值:false
                            .enableTableFieldAnnotation()       // 开启生成实体时生成字段注解 默认值 false
                            .logicDeleteColumnName("deleted")   // 逻辑删除字段名
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)    // 数据库表字段映射到实体的命名策略：下划线转驼峰命
                            // .addSuperEntityColumns("creator", "create_time", "updater", "update_time")
                            // .addTableFills(
                            //  new Column("creator", FieldFill.INSERT),
                            //  new Column("updater", FieldFill.INSERT_UPDATE)
                            // )   // 添加表字段填充，"create_time"字段自动填充为插入时间，"modify_time"字段自动填充为插入修改时间
                            .formatFileName("%s")

                            //5.2、Mapper策略配置
                            .mapperBuilder()
                            .enableFileOverride() // 覆盖mapper
                            .superClass(BaseMapper.class)   // 设置父类
                            .mapperAnnotation(Mapper.class)      // 开启 @Mapper 注解
                            // .enableBaseResultMap() //启用 BaseResultMap 生成
                            .formatMapperFileName("%sMapper")   // 格式化 mapper 文件名称
                            .formatXmlFileName("%sMapper") // 格式化 Xml 文件名称

                            //5.3、service 策略配置
                            .serviceBuilder()
                            .enableFileOverride() // 覆盖service
                            .formatServiceFileName("%sService") // 格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                            .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl

                            //5.4、Controller策略配置
                            .controllerBuilder()
                            .enableFileOverride() // 覆盖controller
                            .enableRestStyle()  // 开启生成 @RestController 控制器
                            .formatFileName("%sController"); // 格式化 Controller 类文件名称，%s进行匹配表名，如 UserController
                })
                //6、自定义配置
                .injectionConfig(consumer -> {
                    Map<String, Object> customMap = new HashMap<>();
                    String modulePackage = String.format("%s.%s" , config.getParent(), config.getModuleName());
                    // 视图对象
                    customMap.put("vo" , modulePackage + ".domain.vo");
                    // 数据传输对象
                    customMap.put("dto" , modulePackage + ".domain.dto");
                    // 业务对象
                    customMap.put("bo" , modulePackage + ".domain.bo");
                    // 转化方法
                    customMap.put("convert" , modulePackage + ".convert");
                    consumer.customMap(customMap);
                    // DTO
                    List<CustomFile> customFiles = new ArrayList<>();
                    customFiles.add(new CustomFile.Builder().packageName("domain/bo").fileName("BO.java")
                            .templatePath("/templates/bo/BO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("domain/bo").fileName("SearchBO.java")
                            .templatePath("/templates/bo/searchbo.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("domain/dto").fileName("DTO.java")
                            .templatePath("/templates/dto/DTO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("domain/vo").fileName("VO.java")
                            .templatePath("/templates/vo/VO.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("domain/vo").fileName("SimpleVO.java")
                            .templatePath("/templates/vo/simplevo.java.vm").enableFileOverride().build());
                    customFiles.add(new CustomFile.Builder().packageName("convert").fileName("Convert.java")
                            .templatePath("/templates/convert/Convert.java.vm").enableFileOverride().build());
                    consumer.customFile(customFiles);
                })
                //7、模板
                .templateEngine(new VelocityTemplateEngine())
                //8、执行
                .execute();
    }

}
