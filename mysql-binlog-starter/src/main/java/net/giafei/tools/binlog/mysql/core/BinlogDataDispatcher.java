package net.giafei.tools.binlog.mysql.core;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ////////////////////////////////////////////////////////////////////
 * //                          _ooOoo_
 * //                         o8888888o
 * //                         88" . "88
 * //                         (| ^_^ |)
 * //                         O\  =  /O
 * //                      ____/`---'\____
 * //                    .'  \\|     |//  `.
 * //                   /  \\|||  :  |||//  \
 * //                  /  _||||| -:- |||||-  \
 * //                  |   | \\\  -  /// |   |
 * //                  | \_|  ''\---/''  |   |
 * //                  \  .-\__  `-`  ___/-. /
 * //                ___`. .'  /--.--\  `. . ___
 * //              ."" '<  `.___\_<|>_/___.'  >'"".
 * //            | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * //            \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //      ========`-.____`-.___\_____/___.-`____.-'========
 * //                           `=---='
 * //      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * //         佛祖保佑       永无BUG     永不修改
 * ////////////////////////////////////////////////////////////////////
 *
 * @author xjf
 * @version 1.0
 * Date 2018/9/20 17:24
 */

public class BinlogDataDispatcher implements BinaryLogClient.EventListener {
    private Map<Long, MySqlTable> tableNameMap = new HashMap<>();
    private Map<String, List<DataListenerContainer>> listenerMap = new HashMap<>();

    public void addListener(String database, String table, List<DataListenerContainer> listeners) {
        String key = database + "." + table;
        this.listenerMap.put(key, listeners);
    }

    @Override
    public void onEvent(Event event) {
        EventHeaderV4 header = event.getHeader();

        EventType eventType = header.getEventType();
        if (eventType == EventType.TABLE_MAP) {
            MySqlTable table = new MySqlTable(event.getData());
            String key = table.getDatabase() + "." + table.getTable();

            if (this.listenerMap.containsKey(key))
                tableNameMap.put(table.getId(), table);
        } else if (eventType == EventType.EXT_UPDATE_ROWS) {
            UpdateRowsEventData data = event.getData();
            if (!tableNameMap.containsKey(data.getTableId()))
                return;

            dispatchEvent(data);
        } else if (eventType == EventType.EXT_WRITE_ROWS) {
            WriteRowsEventData data = event.getData();
            if (!tableNameMap.containsKey(data.getTableId()))
                return;
            dispatchEvent(data);
        } else if (eventType == EventType.EXT_DELETE_ROWS) {
            DeleteRowsEventData data = event.getData();
            if (!tableNameMap.containsKey(data.getTableId()))
                return;

            dispatchEvent(data);
        }
    }

    private void dispatchEvent(UpdateRowsEventData data) {
        MySqlTable table = tableNameMap.get(data.getTableId());
        String key = table.getDatabase() + "." + table.getTable();

        List<DataListenerContainer> containers = listenerMap.get(key);
        List<Map.Entry<Serializable[], Serializable[]>> rows = data.getRows();
        containers.forEach(c -> c.invokeUpdate(rows));
    }

    private void dispatchEvent(DeleteRowsEventData data) {
        MySqlTable table = tableNameMap.get(data.getTableId());
        String key = table.getDatabase() + "." + table.getTable();

        List<DataListenerContainer> containers = listenerMap.get(key);
        List<Serializable[]> rows = data.getRows();
        containers.forEach(c -> c.invokeDelete(rows));
    }

    private void dispatchEvent(WriteRowsEventData data) {
        MySqlTable table = tableNameMap.get(data.getTableId());
        String key = table.getDatabase() + "." + table.getTable();

        List<DataListenerContainer> containers = listenerMap.get(key);
        List<Serializable[]> rows = data.getRows();
        containers.forEach(c -> c.invokeInsert(rows));
    }
}
