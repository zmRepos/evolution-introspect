package com.evolution.introspect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ZhuMing
 * @date 2024/5/21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MbpGeneratorConfig {

    private Integer step = 0;

    private String jdbcUrl = "jdbc:mysql://localhost:3306";

    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    private String username = "ZhuMing";

    private String password = "123456";

    private String database;

    private List<String> databases;

    private List<String> tableNames;

    private List<TableInfo> tableInfos;

    private String tablePrefix = "sys_,t_,c_,de_";

    private String parent = "cn.nju";

    private String moduleName = "";

    private String author = "ZhuMing";

    public String getFullJdbcUrl() {
        return jdbcUrl + "/" + database;
    }

}
