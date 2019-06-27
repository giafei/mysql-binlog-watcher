package net.giafei.tools.demo.watcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.giafei.tools.binlog.mysql.annotation.MysqlWatcher;
import net.giafei.tools.binlog.mysql.listener.IMysqlDataListener;
import net.giafei.tools.demo.entity.TestTable1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MysqlWatcher(hostName = "test-db", database = "test_binlog", table = "test_table1")
public class TestTable1Watcher implements IMysqlDataListener<TestTable1> {

    private Logger logger = LoggerFactory.getLogger(TestTable1Watcher.class);

    @Override
    public void onUpdate(TestTable1 from, TestTable1 to) {
        logger.info("ID 为 {} 的条目数据变更", from.getId());
        logger.info("\t变化前:" + JSON.toJSONString(from, SerializerFeature.WriteDateUseDateFormat));
        logger.info("\t变化后:" + JSON.toJSONString(to, SerializerFeature.WriteDateUseDateFormat));
    }

    @Override
    public void onInsert(TestTable1 data) {
        logger.info("插入ID为 {} 的数据", data.getId());
    }

    @Override
    public void onDelete(TestTable1 data) {
        logger.info("ID 为 {} 的数据被删除", data.getId());
    }
}
