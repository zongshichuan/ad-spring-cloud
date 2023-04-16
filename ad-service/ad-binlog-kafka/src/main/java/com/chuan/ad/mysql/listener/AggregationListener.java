package com.chuan.ad.mysql.listener;

import com.chuan.ad.mysql.TemplateHolder;
import com.chuan.ad.dto.BinlogRowData;
import com.chuan.ad.dto.TableTemplate;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 *继承EventListener接口，重写onEvent方法
 *
 * 把这个自定义的AggregationListener，通过BinaryLogClient.registerEventListener(AggregationListener)注册，
 * 当监听到Binlog事件，就会调用它(AggregationListener)的onEvent(Event event)方法
 */
@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {

    private String dbName;
    private String tableName;
    //<dbName + ":" + tableName,Ilistener>
    private Map<String, Ilistener> listenerMap = new HashMap<>();

    private final TemplateHolder templateHolder;

    @Autowired
    public AggregationListener(TemplateHolder templateHolder) {
        this.templateHolder = templateHolder;
    }

    private String genKey(String dbName, String tableName) {
        return dbName + ":" + tableName;
    }

    public void register(String _dbName, String _tableName,
                         Ilistener ilistener) {
        log.info("register : {}-{}", _dbName, _tableName);
        this.listenerMap.put(genKey(_dbName, _tableName), ilistener);
    }

    @Override
    public void onEvent(Event event) {
        //1、获取Binlog中的事件类型(有些类型我们是不处理的)
        EventType type = event.getHeader().getEventType();
        log.debug("event type: {}", type);

        //2、获取tableName和dbName
        if (type == EventType.TABLE_MAP) {
            //event中包含了EventHeader和EventData，EventData中包含了操作数据
            TableMapEventData data = event.getData();
            this.tableName = data.getTable();
            this.dbName = data.getDatabase();
            return;
        }
        //3、不是这些类型，我们不处理
        if (type != EventType.EXT_UPDATE_ROWS
                && type != EventType.EXT_WRITE_ROWS
                && type != EventType.EXT_DELETE_ROWS) {
            return;
        }

        // 表名和库名是否已经完成填充
        if (StringUtils.isEmpty(dbName) || StringUtils.isEmpty(tableName)) {
            log.error("no meta data event");
            return;
        }

        // 4、找出注册过的监听器
        String key = genKey(this.dbName, this.tableName);
        Ilistener listener = this.listenerMap.get(key);
        if (null == listener) {
            log.debug("skip {}", key);
            return;
        }

        log.info("trigger event: {}", type.name());

        try {
            //5、把EventData转为BinlogRowData
            BinlogRowData rowData = buildRowData(event.getData());
            if (rowData == null) {
                return;
            }

            rowData.setEventType(type);
            listener.onEvent(rowData);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        } finally {
            //清空
            this.dbName = "";
            this.tableName = "";
        }
    }

    /**
     * 统一类型数据处理
     *
     * @param eventData
     * @return
     */
    private List<Serializable[]> getAfterValues(EventData eventData) {
        //插入类型事件
        if (eventData instanceof WriteRowsEventData) {
            return ((WriteRowsEventData) eventData).getRows();
        }
        /**
         * UpdateRowsEventData{tableId=109, includedColumnsBeforeUpdate={0, 1, 2, 3, 4, 5, 6, 7}, includedColumns={0, 1, 2, 3, 4, 5, 6, 7}, rows=[
         *     {before=[1, 1, 奔驰, 1, Thu Apr 13 02:52:53 CST 2023, Fri Apr 28 02:52:57 CST 2023, Thu Jan 01 08:00:00 CST 1970, Thu Jan 01 08:00:00 CST 1970],
         *     after=[1, 1, 奔驰, 2, Thu Apr 13 02:52:53 CST 2023, Fri Apr 28 02:52:57 CST 2023, Thu Jan 01 08:00:00 CST 1970, Thu Jan 01 08:00:00 CST 1970]}
         * ]}
         */
        //update类型事件，仅仅获取after
        if (eventData instanceof UpdateRowsEventData) {
            return ((UpdateRowsEventData) eventData).getRows().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
        }
        //删除类型事件
        if (eventData instanceof DeleteRowsEventData) {
            return ((DeleteRowsEventData) eventData).getRows();
        }

        return Collections.emptyList();
    }

    /**
     * 把EventData转为BinlogRowData
     *
     * @param eventData
     * @return
     */
    private BinlogRowData buildRowData(EventData eventData) {

        TableTemplate table = templateHolder.getTable(tableName);

        if (null == table) {
            log.warn("table {} not found", tableName);
            return null;
        }
        //[<列名，列值(即改变后的值)>]
        List<Map<String, String>> afterMapList = new ArrayList<>();

        for (Serializable[] after : getAfterValues(eventData)) {

            Map<String, String> afterMap = new HashMap<>();

            int colLen = after.length;

            for (int ix = 0; ix < colLen; ++ix) {

                // 取出当前位置对应的列名
                String colName = table.getPosMap().get(ix);

                // 如果没有则说明不关心这个列
                if (null == colName) {
                    log.debug("ignore position: {}", ix);
                    continue;
                }

                String colValue = after[ix].toString();
                afterMap.put(colName, colValue);
            }

            afterMapList.add(afterMap);
        }

        BinlogRowData rowData = new BinlogRowData();
        rowData.setAfter(afterMapList);
        rowData.setTable(table);

        return rowData;
    }
}
