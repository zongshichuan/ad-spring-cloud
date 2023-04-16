package com.chuan.ad.search.vo;

import com.chuan.ad.index.creative.CreativeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检索服务响应对象的定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    //<广告位编码adSlotCode，List<Creative>>
    public Map<String, List<Creative>> adSlot2Ads = new HashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Creative {
        /*广告id*/
        private Long adId;
        /*广告url*/
        private String adUrl;
        private Integer width;
        private Integer height;
        /*广告类型*/
        private Integer type;
        /*子类型*/
        private Integer materialType;

        // 展示监测 url
        private List<String> showMonitorUrl =
                Arrays.asList("www.imooc.com", "www.imooc.com");
        // 点击监测 url
        private List<String> clickMonitorUrl =
                Arrays.asList("www.imooc.com", "www.imooc.com");
    }

    public static Creative convert(CreativeObject object) {

        Creative creative = new Creative();
        creative.setAdId(object.getAdId());
        creative.setAdUrl(object.getAdUrl());
        creative.setWidth(object.getWidth());
        creative.setHeight(object.getHeight());
        creative.setType(object.getType());
        creative.setMaterialType(object.getMaterialType());

        return creative;
    }
}
