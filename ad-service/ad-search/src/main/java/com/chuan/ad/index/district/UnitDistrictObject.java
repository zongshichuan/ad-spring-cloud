package com.chuan.ad.index.district;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地域限制--索引对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDistrictObject {
    /**
     * 推广单元id
     */
    private Long unitId;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;

    // <String, Set<Long>>
    // province-city
}
