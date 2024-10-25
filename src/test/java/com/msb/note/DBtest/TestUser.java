package com.msb.note.DBtest;

import com.msb.note.dao.BaseDao;
import com.msb.note.dao.UserDao;
import com.msb.note.po.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUser {
    @Test
    public void ByName(){
        User user = new UserDao().queryUserByName("admin");
        System.out.println(user.getMood());
    }
    @Test
    public void ByName2(){
        User user = new UserDao().queryUserByName2("admin");
        System.out.println(user.getUname());
    }

    @Test
    public void testAdd() {
        String sql = "insert into tb_user (uname,upwd,nick,head,mood) values (?,?,?,?,?)";
        List<Object> params = new ArrayList<>();
        params.add("lisi");
        params.add("e10adc3949ba59abbe56e057f20f883e");
        params.add("lisi");
        params.add("404.jpg");
        params.add("Hello");
        int row = BaseDao.executeUpdate(sql,params);
        System.out.println(row);

    }
}
