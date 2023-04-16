package com.chuan.ad.dto;

import com.chuan.ad.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方便操作读取表的信息
 *
 * (表名tableName，级别level,<<操作类型(insert/update/delete),...>，clolumn集合>，<<字段索引,字段名>,....>)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableTemplate {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 级别
     */
    private String level;

    /**
     * <操作类型(insert/update/delete)，clolumn集合>
     */
    private Map<OpType, List<String>> opTypeFieldSetMap = new HashMap<>();

    /**
     * binlog中记录的是字段索引 ，不会记录字段名 ，所以这里封装一个map映射
     *
     * 字段索引 -> 字段名
     * */
    private Map<Integer, String> posMap = new HashMap<>();
}
