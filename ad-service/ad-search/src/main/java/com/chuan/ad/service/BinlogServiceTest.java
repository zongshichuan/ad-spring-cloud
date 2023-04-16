package com.chuan.ad.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

/**
 * Binlog监听操作
 */
public class BinlogServiceTest {

    /**
     * 监听结果显示
     *
     * Delete--------------
     * DeleteRowsEventData{tableId=109, includedColumns={0, 1, 2, 3, 4, 5, 6, 7}, rows=[
     *     [1, 1, 宝马x8, 1, Sat Apr 15 01:47:50 CST 2023, Sun Apr 30 01:47:54 CST 2023, Thu Jan 01 08:00:00 CST 1970, Thu Jan 01 08:00:00 CST 1970]
     * ]}
     * Write---------------
     * WriteRowsEventData{tableId=109, includedColumns={0, 1, 2, 3, 4, 5, 6, 7}, rows=[
     *     [1, 1, 奔驰, 1, Thu Apr 13 02:52:53 CST 2023, Fri Apr 28 02:52:57 CST 2023, Thu Jan 01 08:00:00 CST 1970, Thu Jan 01 08:00:00 CST 1970]
     * ]}
     * Update--------------
     * UpdateRowsEventData{tableId=109, includedColumnsBeforeUpdate={0, 1, 2, 3, 4, 5, 6, 7}, includedColumns={0, 1, 2, 3, 4, 5, 6, 7}, rows=[
     *     {before=[1, 1, 奔驰, 1, Thu Apr 13 02:52:53 CST 2023, Fri Apr 28 02:52:57 CST 2023, Thu Jan 01 08:00:00 CST 1970, Thu Jan 01 08:00:00 CST 1970],
     *     after=[1, 1, 奔驰, 2, Thu Apr 13 02:52:53 CST 2023, Fri Apr 28 02:52:57 CST 2023, Thu Jan 01 08:00:00 CST 1970, Thu Jan 01 08:00:00 CST 1970]}
     * ]}
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        BinaryLogClient client = new BinaryLogClient(
                "127.0.0.1",
                3306,
                "root",
                "123456"
        );
//        指定binlog日志文件监听  默认从最新的日志文件监听
//        client.setBinlogFilename("binlog.000037");
//        指定监听的位置
//        client.setBinlogPosition();

        //监听器：监听到mysql数据表变化的数据
        client.registerEventListener(event -> {

            EventData data = event.getData();
            //更新数据事件
            if (data instanceof UpdateRowsEventData) {
                System.out.println("Update--------------");
                System.out.println(data.toString());
            }
            //写数据事件
            else if (data instanceof WriteRowsEventData) {
                System.out.println("Write---------------");
                System.out.println(data.toString());
            }
            //删除数据事件
            else if (data instanceof DeleteRowsEventData) {
                System.out.println("Delete--------------");
                System.out.println(data.toString());
            }
        });

        client.connect();
    }
}
