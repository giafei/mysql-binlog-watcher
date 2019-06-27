package net.giafei.tools.binlog.mysql.thread;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import net.giafei.tools.binlog.mysql.config.MySqlHost;
import net.giafei.tools.binlog.mysql.core.BinlogDataDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
 * Date 2018/9/21 13:35
 */

public class BinLogListenerThread implements Runnable {
    private MySqlHost host;
    private BinlogDataDispatcher listener;

    private Logger logger = LoggerFactory.getLogger(BinLogListenerThread.class);

    public BinLogListenerThread(MySqlHost host, BinlogDataDispatcher listener) {
        this.host = host;
        this.listener = listener;
    }

    @Override
    public void run() {
        BinaryLogClient client = new BinaryLogClient(host.getHost(), host.getPort(), host.getUsername(), host.getPassword());

        client.registerEventListener(listener);

        while (true) {
            try {
                client.connect();
            } catch (IOException e) {
                logger.error("{}:{}监听器错误", host.getHost(), host.getPort(), e);
            }
        }
    }
}
