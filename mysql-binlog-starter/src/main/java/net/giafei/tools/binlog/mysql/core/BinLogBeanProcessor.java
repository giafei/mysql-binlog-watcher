package net.giafei.tools.binlog.mysql.core;

import net.giafei.tools.binlog.mysql.config.MySqlHostProfile;
import net.giafei.tools.binlog.mysql.listener.IMysqlDataListener;
import net.giafei.tools.binlog.mysql.thread.BinlogThreadStarter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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
 * Date 2018/9/20 17:22
 */

@Component
public class BinLogBeanProcessor implements SmartInitializingSingleton {
    private ApplicationContext context;

    @Autowired
    private MySqlHostProfile profile;

    public BinLogBeanProcessor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, IMysqlDataListener> beans = context.getBeansOfType(IMysqlDataListener.class);

        Map<String, List<MysqlDataListenerData>> listeners = beans.values().stream()
                .map(MysqlDataListenerData::new)
                .collect(Collectors.groupingBy(MysqlDataListenerData::getHostName));

        listeners.forEach((k, v) -> new BinlogThreadStarter().runThread(profile.getByNameAndThrow(k), v));
    }
}
