package com.chuan.ad.sender;


import com.chuan.ad.dto.MySqlRowData;

/**
 * 增量数据的投递接口
 */
public interface ISender {

    void sender(MySqlRowData rowData);
}
