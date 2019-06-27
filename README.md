基于Spring Boot的监听Mysql数据变动的工具

使用时需要在配置文件中配置数据库的连接信息并配置binlog格式为ROW

使用方式
```JAVA
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
```

可以监听mysql的数据变化同步数据到Redis或其他缓存，从业务层解耦
