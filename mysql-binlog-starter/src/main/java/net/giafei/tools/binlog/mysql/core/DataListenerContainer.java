package net.giafei.tools.binlog.mysql.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import net.giafei.tools.binlog.mysql.listener.IMysqlDataListener;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
 * Date 2018/9/21 9:57
 */

public class DataListenerContainer<T> {
    private Class<T> entityClass;
    private IMysqlDataListener<T> listener;
    private String[] columnName;

    //从binlog读取的时间与真实值有偏差 与 服务端的时区有关
    private long timeOffset = 0;

    private static final ParserConfig snakeCase;
    static {
        snakeCase = new ParserConfig();
        snakeCase.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    public DataListenerContainer(Class<T> entityClass, IMysqlDataListener<T> listener, String[] columnName) {
        this.entityClass = entityClass;
        this.listener = listener;
        this.columnName = columnName;
    }

    public DataListenerContainer(Class<T> entityClass, IMysqlDataListener<T> listener, String[] columnName, long timeOffset) {
        this.entityClass = entityClass;
        this.listener = listener;
        this.columnName = columnName;
        this.timeOffset = timeOffset;
    }

    public void setTimeOffset(long timeOffset) {
        this.timeOffset = timeOffset;
    }

    public void invokeInsert(List<Serializable[]> data) {
        invokeSingle(data, listener::onInsert);
    }

    public void invokeDelete(List<Serializable[]> data) {
        invokeSingle(data, listener::onDelete);
    }

    public void invokeUpdate(List<Map.Entry<Serializable[], Serializable[]>> data) {
        data.forEach(row -> {
            listener.onUpdate(toEntity(row.getKey()), toEntity(row.getValue()));
        });
    }

    private T toEntity(Serializable[] data) {
        for (int i = 0; i < data.length; i++) {
            Serializable da = data[i];
            if (da instanceof Date) {
                data[i] = new Date(((Date)da).getTime() + timeOffset);
            }
        }

        Map<String, Object> b = new HashMap<>(data.length);
        for (int i = 0; i < data.length; i++) {
            b.put(columnName[i], data[i]);
        }

        return TypeUtils.cast(b, entityClass, snakeCase);
    }

    private void invokeSingle(List<Serializable[]> data, Consumer<T> consumer) {
        data.stream().map(this::toEntity)
                .forEach(consumer);
    }
}
