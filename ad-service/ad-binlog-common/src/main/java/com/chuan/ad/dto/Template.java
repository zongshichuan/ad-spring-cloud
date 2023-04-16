package com.chuan.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对应resources下模板文件template.json中数据库的内容 对应实体类
 *
 * (数据库名,【JsonTable,...】)
 *
 * 数据库级别
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Template {

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 数据库表的信息
     */
    private List<JsonTable> tableList;
}
