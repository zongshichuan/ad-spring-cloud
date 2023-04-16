package com.chuan.ad.mysql.listener;


import com.chuan.ad.dto.BinlogRowData;

/**
 * Binlog监听接口
 */
public interface Ilistener {

    void register();

    void onEvent(BinlogRowData eventData);
}
