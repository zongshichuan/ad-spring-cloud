package com.chuan.ad.search.vo.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 请求匹配信息--->地域
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictFeature {

    private List<ProvinceAndCity> districts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProvinceAndCity {
        /*省份*/
        private String province;
        /*城市*/
        private String city;
    }
}
