package com.chuan.ad.search;

import com.chuan.ad.search.vo.SearchRequest;
import com.chuan.ad.search.vo.SearchResponse;

public interface ISearch {
    SearchResponse fetchAds(SearchRequest request);
}
