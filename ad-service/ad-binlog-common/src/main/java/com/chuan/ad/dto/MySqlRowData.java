package com.chuan.ad.dto;

import com.chuan.ad.constant.OpType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 增量数据的投递
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySqlRowData {

    private String tableName;

    private String level;

    /**
     * 操作数据的类型
     */
    private OpType opType;

    //[<列名，列值(即变动后的列值)>，。。。]
    private List<Map<String, String>> fieldValueMap = new ArrayList<>();
}
