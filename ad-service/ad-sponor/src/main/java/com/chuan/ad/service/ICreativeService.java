package com.chuan.ad.service;


import com.chuan.ad.vo.CreativeRequest;
import com.chuan.ad.vo.CreativeResponse;

public interface ICreativeService {

    CreativeResponse createCreative(CreativeRequest request);
}
