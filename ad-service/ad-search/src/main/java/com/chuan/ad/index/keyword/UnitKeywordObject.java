package com.chuan.ad.index.keyword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关键词限制--索引对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitKeywordObject {
    /**
     * 推广单元id
     */
    private Long unitId;
    /**
     * 关键词
     */
    private String keyword;
}
