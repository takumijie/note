package com.msb.note.dao;

import com.msb.note.po.User;
import com.msb.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public User queryUserByName2(String userName){
        String sql = "select * from tb_user where uname = ?";
        List<Object> params = new ArrayList<>();
        params.add(userName);
        User user = (User) BaseDao.queryRow(sql,params,User.class);
        return user;
    }
    /*
    * 通过用户名查询用户对象
    * */
    public User queryUserByName(String userName) {
        User user = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            String sql = "select * from tb_user where uname = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,userName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                user = new User();
                user.setUserId(resultSet.getInt("userId"));
                user.setUname(resultSet.getString("uname"));
                user.setUpwd(resultSet.getString("upwd"));
                user.setNick(resultSet.getString("nick"));
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }


    public User queryUserByNickAndUserId(String nick, Integer userId) {
        // 1. 定义SQL语句
        String sql = "select * from tb_user where nick = ? and userId != ?";
        // 2. 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userId);
        // 3. 调用BaseDao的查询方法
        User user = (User) BaseDao.queryRow(sql, params, User.class);
        return user;
    }

    public int updateUser(User user) {
        // 1. 定义SQL语句
        String sql = "update tb_user set nick = ?, mood = ?, head = ? where userId = ? ";
        // 2. 设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        // 3. 调用BaseDao的更新方法，返回受影响的行数
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }
}
