package com.chuan.ad.search.vo;

import com.chuan.ad.search.vo.feature.DistrictFeature;
import com.chuan.ad.search.vo.feature.FeatureRelation;
import com.chuan.ad.search.vo.feature.ItFeature;
import com.chuan.ad.search.vo.feature.KeywordFeature;
import com.chuan.ad.search.vo.media.AdSlot;
import com.chuan.ad.search.vo.media.App;
import com.chuan.ad.search.vo.media.Device;
import com.chuan.ad.search.vo.media.Geo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *检索服务请求对象的定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    // 媒体方的请求标识
    private String mediaId;
    // 请求基本信息
    private RequestInfo requestInfo;
    // 匹配信息
    private FeatureInfo featureInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {
        /*请求唯一id*/
        private String requestId;
        /*广告位信息*/
        private List<AdSlot> adSlots;
        /*请求方 终端信息*/
        private App app;
        /*地理位置信息*/
        private Geo geo;
        /*设备信息*/
        private Device device;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureInfo {
        /*请求匹配信息--->关键词*/
        private KeywordFeature keywordFeature;
        /*请求匹配信息--->地域*/
        private DistrictFeature districtFeature;
        /*请求匹配信息--->兴趣*/
        private ItFeature itFeature;
        /**
         * 请求匹配信息--->各个feature(关键词、地域、兴趣)之间的关系
         *
         * 默认是AND
         * */
        private FeatureRelation relation = FeatureRelation.AND;
    }
}
