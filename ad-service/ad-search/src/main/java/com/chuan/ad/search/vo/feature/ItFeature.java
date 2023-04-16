package com.chuan.ad.search.vo.feature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 请求匹配信息--->兴趣
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItFeature {

    private List<String> its;
}
