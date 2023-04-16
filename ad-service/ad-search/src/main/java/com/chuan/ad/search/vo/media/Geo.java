package com.chuan.ad.search.vo.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地理位置信息
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Geo {
    /*精度*/
    private Float latitude;
    /*维度*/
    private Float longitude;
    /*城市*/
    private String city;
    /*省份*/
    private String province;
}


















