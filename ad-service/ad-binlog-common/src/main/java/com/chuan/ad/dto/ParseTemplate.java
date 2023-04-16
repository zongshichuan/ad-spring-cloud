package com.chuan.ad.dto;

import com.chuan.ad.constant.OpType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 解析模板文件template.json
 *
 * (数据库名,【<<表名，TableTemplate表的信息>>，...】)
 */
@Data
public class ParseTemplate {

    /**
     * 数据库的名称
     */
    private String database;

    /**
     *<表名，TableTemplate表的信息>
     */
    private Map<String, TableTemplate> tableTemplateMap = new HashMap<>();

    /**
     * 解析_template
     *
     * @param _template
     * @return
     */
    public static ParseTemplate parse(Template _template) {
        //1、封装ParseTemplate   需要database和<tableName, TableTemplate>
        ParseTemplate template = new ParseTemplate();
        template.setDatabase(_template.getDatabase());

        for (JsonTable table : _template.getTableList()) {

            String name = table.getTableName();
            Integer level = table.getLevel();
            //封装TableTemplate  需要tableName、level、<<操作类型(insert/update/delete),...>，clolumn集合>、<<字段索引,字段名>,....>
            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setTableName(name);
            tableTemplate.setLevel(level.toString());

            template.tableTemplateMap.put(name, tableTemplate);

            // 2、填充tableTemplate的<<操作类型(insert/update/delete),...>，clolumn集合>属性
            Map<OpType, List<String>> opTypeFieldSetMap =
                    tableTemplate.getOpTypeFieldSetMap();

            for (JsonTable.Column column : table.getInsert()) {
                getAndCreateIfNeed(
                        OpType.ADD,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }
            for (JsonTable.Column column : table.getUpdate()) {
                getAndCreateIfNeed(
                        OpType.UPDATE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }
            for (JsonTable.Column column : table.getDelete()) {
                getAndCreateIfNeed(
                        OpType.DELETE,
                        opTypeFieldSetMap,
                        ArrayList::new
                ).add(column.getColumn());
            }
        }

        return template;
    }

    private static <T, R> R getAndCreateIfNeed(T key, Map<T, R> map,
                                               Supplier<R> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }
}
