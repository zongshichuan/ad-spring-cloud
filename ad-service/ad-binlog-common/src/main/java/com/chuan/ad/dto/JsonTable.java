package com.chuan.ad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对应resources下模板文件template.json中表的内容  对应实体类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JsonTable {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 插入操作
     */
    private List<Column> insert;
    /**
     * 修改操作
     */
    private List<Column> update;
    /**
     * 删除操作
     */
    private List<Column> delete;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Column {

        private String column;

    }
}
