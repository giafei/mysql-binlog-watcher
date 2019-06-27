package net.giafei.tools.binlog.mysql.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

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
 * Date 2018/9/20 17:09
 */

@Configuration
@ConfigurationProperties(prefix = "binlog.mysql")
public class MySqlHostProfile {
    private List<MySqlHost> hosts;

    public List<MySqlHost> getHosts() {
        return hosts;
    }

    public void setHosts(List<MySqlHost> hosts) {
        this.hosts = hosts;
    }

    public Optional<MySqlHost> getByName(String name) {
        return hosts.stream().filter(v -> name.equals(v.getName()))
                .findAny();
    }

    public MySqlHost getByNameAndThrow(String name) {
        return getByName(name).orElseThrow(() -> new RuntimeException("未配置名为 "+name+" 的 binlog 连接信息"));
    }
}
