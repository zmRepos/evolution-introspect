package com.evolution.introspect.controller;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.evolution.introspect.model.MbpGeneratorConfig;
import com.evolution.introspect.model.TableInfo;
import com.evolution.introspect.pojo.CommonResult;
import com.evolution.introspect.util.MbpGeneratorUtil;
import com.zaxxer.hikari.HikariDataSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器控制器
 * 提供数据库连接、数据库列表获取、表信息获取和代码生成功能
 *
 * @author ZhuMing
 * @date 2024/5/21
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "代码生成", description = "生成符合")
public class GenerateController {

    private final DataSourceProperties dataSourceProperties;

    /**
     * 显示代码生成器主页面
     *
     * @return 主页面数据的JSON响应
     */
    @GetMapping("/")
    @ResponseBody
    @Operation(summary = "显示代码生成器主页面")
    public CommonResult<MbpGeneratorConfig> index() {
        MbpGeneratorConfig dbConfig = new MbpGeneratorConfig();

        // 如果存在默认数据源配置，则初始化表单默认值
        if (dataSourceProperties != null) {
            dbConfig.setJdbcUrl(dataSourceProperties.getUrl());
            dbConfig.setDriverClassName(dataSourceProperties.getDriverClassName());
            dbConfig.setUsername(dataSourceProperties.getUsername());
            dbConfig.setPassword(dataSourceProperties.getPassword());
        }

        return CommonResult.success(dbConfig);
    }


    /**
     * 测试数据库连接并获取数据库列表
     *
     * @param dbConfig 数据库连接配置
     * @return 包含数据库列表的响应结果
     */
    @PostMapping("/databases")
    @ResponseBody
    @Operation(summary = "测试数据库连接并获取数据库列表")
    public CommonResult<MbpGeneratorConfig> getDatabases(@RequestBody MbpGeneratorConfig dbConfig) {
        log.info("获取数据库列表，配置信息: {}" , dbConfig);

        // 验证数据库连接
        if (!connectionValid(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            log.warn("数据库连接失败，配置信息: {}" , dbConfig);
            return CommonResult.error(400, "数据库连接失败");
        }

        // 查询数据库列表
        List<String> databases = queryDatabaseList(dbConfig);
        dbConfig.setDatabases(databases);
        dbConfig.setStep(1);
        log.info("成功获取 {} 个数据库" , databases.size());
        return CommonResult.success(dbConfig);
    }

    /**
     * 获取指定数据库中的表信息
     *
     * @param dbConfig 数据库配置（包含选定的数据库）
     * @return 包含表信息列表的响应结果
     */
    @PostMapping("/tables")
    @ResponseBody
    @Operation(summary = "获取指定数据库中的表信息")
    public CommonResult<MbpGeneratorConfig> getTables(@RequestBody MbpGeneratorConfig dbConfig) {
        log.info("获取表信息，配置信息: {}" , dbConfig);

        // 验证数据库连接
        if (!connectionValid(dbConfig.getJdbcUrl(), dbConfig.getUsername(), dbConfig.getPassword())) {
            log.warn("数据库连接失败，配置信息: {}" , dbConfig);
            return CommonResult.error(400, "数据库连接失败");
        }

        // 查询表信息
        List<TableInfo> tableInfos = queryTableInfos(dbConfig);
        dbConfig.setTableInfos(tableInfos);
        dbConfig.setStep(2);

        log.info("成功获取 {} 个表信息" , tableInfos.size());
        return CommonResult.success(dbConfig);
    }

    /**
     * 执行代码生成操作
     *
     * @param dbConfig 代码生成配置
     * @return 生成结果响应
     */
    @PostMapping("/done")
    @ResponseBody
    @Operation(summary = "获取指定数据库中的表信息")
    public CommonResult<MbpGeneratorConfig> generateCode(@RequestBody MbpGeneratorConfig dbConfig) {
        log.info("开始生成代码，配置信息: {}" , dbConfig);
        try {
            // 执行代码生成
            MbpGeneratorUtil.generate(dbConfig);
            dbConfig.setStep(3);
            log.info("代码生成成功");
            return CommonResult.success(dbConfig);
        } catch (Exception e) {
            log.error("代码生成失败" , e);
            return CommonResult.error(500, "代码生成失败: " + e.getMessage());
        }
    }

    /**
     * 验证数据库连接是否有效
     *
     * @param url      数据库连接URL
     * @param username 用户名
     * @param password 密码
     * @return 连接是否有效
     */
    @PostMapping("/test")
    @Operation(summary = "验证数据库连接是否有效")
    public boolean connectionValid(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            log.error("数据库连接验证失败: url={}, username={}" , url, username, ex);
            return false;
        }
    }

    /**
     * 查询数据库列表
     *
     * @param dbConfig 数据库配置
     * @return 数据库名称列表
     */
    private List<String> queryDatabaseList(MbpGeneratorConfig dbConfig) {
        HikariDataSource dataSource = createDataSource(dbConfig);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Map<String, Object>> results = jdbcTemplate.queryForList("SHOW DATABASES");
            List<String> databases = Lists.newArrayList();

            for (Map<String, Object> row : results) {
                String database = (String) row.get("Database");
                databases.add(database);
            }

            return databases;
        } finally {
            dataSource.close();
        }
    }

    /**
     * 查询表信息列表
     *
     * @param dbConfig 数据库配置
     * @return 表信息列表
     */
    private List<TableInfo> queryTableInfos(MbpGeneratorConfig dbConfig) {
        HikariDataSource dataSource = createDataSource(dbConfig);
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(
                    dbConfig.getJdbcUrl(),
                    dbConfig.getUsername(),
                    dbConfig.getPassword())
                    .build();

            IDbQuery dbQuery = dataSourceConfig.getDbQuery();
            List<Map<String, Object>> results = jdbcTemplate.queryForList(dbQuery.tablesSql());
            List<TableInfo> tableInfos = Lists.newArrayList();

            for (Map<String, Object> table : results) {
                TableInfo tableInfo = new TableInfo();
                tableInfo.setName((String) table.get(dbQuery.tableName()));
                tableInfo.setComment((String) table.get(dbQuery.tableComment()));
                tableInfos.add(tableInfo);
            }
            return tableInfos;
        } finally {
            dataSource.close();
        }
    }

    /**
     * 创建数据源连接（不指定数据库）
     *
     * @param dbConfig 数据库配置
     * @return Hikari数据源
     */
    private HikariDataSource createDataSource(MbpGeneratorConfig dbConfig) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbConfig.getJdbcUrl());
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPassword(dbConfig.getPassword());
        dataSource.setDriverClassName(dbConfig.getDriverClassName());
        return dataSource;
    }
}
