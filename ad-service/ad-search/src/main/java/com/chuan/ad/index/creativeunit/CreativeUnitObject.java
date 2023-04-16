package com.chuan.ad.index.creativeunit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 广告创意和推广单元关联关系--索引对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeUnitObject {
    /**
     * 广告创意id
     */
    private Long adId;
    /**
     * 推广单元id
     */
    private Long unitId;

    // adId-unitId
}
