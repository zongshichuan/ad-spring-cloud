package com.chuan.ad.search.vo.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 请求匹配信息--->关键词
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordFeature {
    /*关键词*/
    private List<String> keywords;
}
