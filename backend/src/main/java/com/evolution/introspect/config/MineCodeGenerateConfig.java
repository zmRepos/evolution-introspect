package com.evolution.introspect.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("code.generate")
@Data
public class MineCodeGenerateConfig {
    /**
     * 数据库用户名
     */
    private String dbUserName;
    
    /**
     * 数据库密码
     */
    private String dbPwd;
    
    /**
     * 数据库连接URL
     */
    private String dbUrl;
    
    /**
     * 代码作者
     */
    private String author;
    
    /**
     * 注释中的日期格式
     */
    private String commentDateFormat;
    
    /**
     * 代码生成目标路径
     */
    private String targetPath;

    /**
     * 表前缀
     */
    private List<String> tablePrefix;

    /**
     * 包名
     */
    private String packagePath;
}
