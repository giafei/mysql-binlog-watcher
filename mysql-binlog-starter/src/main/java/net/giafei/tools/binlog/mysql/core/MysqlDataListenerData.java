package net.giafei.tools.binlog.mysql.core;

import net.giafei.tools.binlog.mysql.annotation.MysqlWatcher;
import net.giafei.tools.binlog.mysql.listener.IMysqlDataListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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
 * Date 2018/9/26 9:15
 */

public class MysqlDataListenerData {
    private IMysqlDataListener listener;
    private String hostName;
    private String database;
    private String table;
    private Class<?> entityClass;

    public MysqlDataListenerData(IMysqlDataListener listener) {
        this.listener = listener;

        Class<?> targetClass = AopUtils.getTargetClass(listener);
        MysqlWatcher annotation = AnnotationUtils.findAnnotation(targetClass, MysqlWatcher.class);
        if (annotation == null)
            throw new RuntimeException("Mysql binlog listener必须添加 MysqlWatcher 注解");

        hostName = annotation.hostName();
        database = annotation.database();
        table = annotation.table();
        entityClass = getGenericClass(targetClass);
    }

    public IMysqlDataListener getListener() {
        return listener;
    }

    public String getHostName() {
        return hostName;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    private Class<?> getGenericClass(Class<?> targetClass) {
        if (targetClass == Object.class)
            return null;

        Type[] types = targetClass.getGenericInterfaces();
        if (types.length == 0) {
            types = new Type[] {targetClass.getGenericSuperclass()};
        }

        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                Type[] array = t.getActualTypeArguments();
                return (Class<?>) array[0];
            }
        }

        return getGenericClass(targetClass.getSuperclass());
    }
}
