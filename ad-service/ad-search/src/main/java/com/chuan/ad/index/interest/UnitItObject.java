package com.chuan.ad.index.interest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 兴趣限制--索引对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitItObject {
    /**
     * 推广单元id
     */
    private Long unitId;
    /**
     * 兴趣标签
     */
    private String itTag;
}
