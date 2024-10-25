package com.msb.note.DBtest;

import com.msb.note.util.DBUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class testdb {
    private Logger logger = LoggerFactory.getLogger(testdb.class);
    @Test
    public void testLA(){
        Connection connection = DBUtil.getConnection();
        logger.info("获取数据库连接"+connection);
    }
}
