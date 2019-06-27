package net.giafei.tools.binlog.mysql.thread;

import net.giafei.tools.binlog.mysql.config.MySqlHost;
import net.giafei.tools.binlog.mysql.core.BinlogDataDispatcher;
import net.giafei.tools.binlog.mysql.core.DataListenerContainer;
import net.giafei.tools.binlog.mysql.core.MysqlDataListenerData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
 * Date 2018/9/21 13:40
 */

public class BinlogThreadStarter {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //设计失误 其实只需要一个connection  已经写成这样了 就先不改了
    private Map<String, Connection> connectionPool = new HashMap<>();
    private Connection getConnection(MySqlHost host) throws SQLException {
        String key = host.getHost() + ":" + host.getPort();
        Connection connection = connectionPool.get(key);

        if (connection == null) {
            String url =  "jdbc:mysql://" + key +
                    "/INFORMATION_SCHEMA?useUnicode=true&characterEncoding=UTF-8&useSSL=false";

            connection = DriverManager.getConnection(url, host.getUsername(), host.getPassword());
            connectionPool.put(key, connection);
        }

        return connection;
    }

    private void releaseConnection() {
        for (Map.Entry<String, Connection> entry : connectionPool.entrySet()) {
            try {
                entry.getValue().close();
            } catch (SQLException e) {
                //不用管
            }
        }

        connectionPool.clear();
    }

    public void runThread(MySqlHost host, List<MysqlDataListenerData> listeners) {
        Map<String, List<MysqlDataListenerData>> map = listeners.stream()
                .collect(Collectors.groupingBy(l -> l.getDatabase() + ":" + l.getTable()));

        BinlogDataDispatcher logListener = new BinlogDataDispatcher();

        map.forEach((k, v) -> {
            String[] arr = k.split(":");
            String[] columns = getColumns(host, arr[0], arr[1]);

            List<DataListenerContainer> containers = v.stream()
                    .map(l -> new DataListenerContainer(l.getEntityClass(), l.getListener(), columns, host.getTimeOffset()))
                    .collect(Collectors.toList());

            logListener.addListener(arr[0], arr[1], containers);
        });

        new Thread(new BinLogListenerThread(host, logListener)).start();

        releaseConnection();
    }

    private String[] getColumns(MySqlHost host, String db, String table) {
        try {
            Connection connection = getConnection(host);
            Statement statement = connection.createStatement();

            String sql = "select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA='"
                    + db + "' and TABLE_NAME='" + table + "' order by ORDINAL_POSITION asc;";

            ResultSet resultSet = statement.executeQuery(sql);
            List<String> buf = new ArrayList<>();

            while (resultSet.next()) {
                buf.add(resultSet.getString(1));
            }

            return buf.toArray(new String[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
