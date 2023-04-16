package com.chuan.ad.dump.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 索引字段需要字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitItTable {

    private Long unitId;
    private String itTag;
}
