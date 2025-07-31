package com.evolution.introspect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZhuMing
 * @date 2024/5/21
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    private String name;

    private String comment;
}
