package com.msb.note.dao;

import com.msb.note.util.DBUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/*
* 基础jdbc操作类
* 更新操作（增删改）
* 查询操作
* 更新操作 （添加、修改、删除）
 *      查询操作
 *          1. 查询一个字段 （只会返回一条记录且只有一个字段；常用场景：查询总数量）
 *          2. 查询集合
 *          3. 查询某个对象
*
* */
public class BaseDao {
//    更新操作
    public static int executeUpdate(String sql, List<Object> params){

 /*         更新操作
           添加、修改、删除
           1. 得到数据库连接
           2. 定义sql语句 （添加语句、修改语句、删除语句）
           3. 预编译
           4. 如果有参数，则设置参数，下标从1开始 （数组或集合、循环设置参数）
           5. 执行更新，返回受影响的行数
           6. 关闭资源
          注：需要两个参数:sql语句、所需参数的集合
         */

        int row = 0;//受影响的行数
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
//            得到连接
            connection = DBUtil.getConnection();
//            预编译sql
            preparedStatement = connection.prepareStatement(sql);
//            如果有参数参数从下标1开始
            if (params != null && params.size() > 0){
//                循环取值，设置参数为Object
                for (int i = 0;i < params.size();i++){
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            row = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBUtil.closeConnection(connection,preparedStatement,null);
        }

        return row;
    }


//     查询一个字段只会返回一条记录
    public static Object findSingleValue(String sql,List<Object> params){
        Object object = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
//            如果有参数参数从下标1开始
            if (params != null && params.size() > 0){
//                循环取值，设置参数为Object
                for (int i = 0;i < params.size();i++){
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                object = resultSet.getObject(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBUtil.closeConnection(connection,preparedStatement,resultSet);
        }
        return object;
    }

//    查询集合
    public static List queryRows(String sql,List<Object> params,Class cls){
        List list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            //            如果有参数参数从下标1开始
            if (params != null && params.size() > 0){
//                循环取值，设置参数为Object
                for (int i = 0;i < params.size();i++){
                    preparedStatement.setObject(i+1,params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();

//            得到结果集的元数据对象
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//            得到查询的字段数量
            int fieldNum = resultSetMetaData.getColumnCount();
//            判断并分析结果集
            while (resultSet.next()){
//             实例化对象
                Object object = cls.newInstance();
//                遍历查询的字段数量，得到数据库查询的每一个列名
                for (int i = 1;i<= fieldNum ; i++){
//                    查询每一个的列名
//                    getColumnLabel(i);获取列名或别名
//                    getColumnName(i);获取列名
                    String columnName = resultSetMetaData.getColumnLabel(i);
//                    反射获取filed对象
                    Field field = cls.getDeclaredField(columnName);
//                    拼接set方法，得到字符串
                    String setMethod = "set"+columnName.substring(0,1).toUpperCase()+columnName.substring(1);
//                    通过反射，将set方法字符串反射为对应类中的set方法
                    Method method = cls.getDeclaredMethod(setMethod,field.getType());
//                    得到每一个字段对应的值
                    Object value = resultSet.getObject(columnName);
//                    通过invoke调用方法
                    method.invoke(object,value);
                }
//                将javabean设置到集合中
                list.add(object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.closeConnection(connection,preparedStatement,resultSet);
        }
        return list;
    }

//    查询某一个对象
    public static Object queryRow(String sql,List<Object> params,Class cls){
        List list = queryRows(sql,params,cls);
        Object object = null;
        if (list != null && list.size()>0){
            object = list.get(0);
        }
        return object;
    }
}
