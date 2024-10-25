package com.msb.note.dao;

import com.msb.note.po.NoteType;
import com.msb.note.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {
    /**
     * 通过用户ID查询类型集合
     *             1. 定义SQL语句
     *                 String sql = "select typeId,typeName,userId from tb_note_type where userId = ? ";
     *             2. 设置参数列表
     *             3. 调用BaseDao的查询方法，返回集合
     *             4. 返回集合
     * @param userId
     * @return
     */
    public List<NoteType> findTypeListByUserId(Integer userId){
//        1. 定义SQL语句
        String sql = "select * from tb_note_type where userId = ?";
        List<NoteType> list = null;
//        2. 设置参数列表
        List<Object> params = new ArrayList<>();
        params.add(userId);
//        3. 调用BaseDao的查询方法，返回集合
        list = BaseDao.queryRows(sql,params,NoteType.class);
        return list;
    }

//    通过类型ID查询云记记录的数量，返回云记数量
    public long findNoteCountByTypeId(String typeId) {
//        定义sql语句
        String sql = "select count(1) from tb_note where typeId = ?";
//        设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(typeId);
//        调用baseDao查询方法
        long count = (long) BaseDao.findSingleValue(sql,params);
        return count;
    }

//    通过类型ID删除指定的类型记录，返回受影响的行数
    public int deleteTypeById(String typeId) {
        //        定义sql语句
        String sql = "delete from tb_note_type where typeId = ?";
//        设置参数集合
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

//    查询当前登录用户下，类型名称是否唯一
    public Integer checkTypeName(String typeName, Integer userId, String typeId) {
        String sql = "select * from tb_note_type where userId = ? and typeName = ?";
//        设置参数集合
        List<Object> params= new ArrayList<>();
        params.add(userId);
        params.add(typeName);
//        查询
        NoteType noteType = (NoteType) BaseDao.queryRow(sql,params,NoteType.class);
//        如果对象为空就表示可用
        if (noteType == null){
            return 1;
        }else {
//            如果是修改操作，需要判断是否是当前记录本身
            if (typeId.equals(noteType.getTypeId().toString())){
                return 1;
            }
        }
        return 0;
    }

//    添加方法，返回主键
    public Integer addType(String typeName, Integer userId) {
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement("insert into tb_note_type (typeName,userId) values (?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,typeName);
            preparedStatement.setInt(2,userId);
            int row = preparedStatement.executeUpdate();
//            判断受影响行数
            if (row > 0){
//                获取主键结果集
                resultSet =  preparedStatement.getGeneratedKeys();
                if (resultSet.next()){
                    key=resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            DBUtil.closeConnection(connection,preparedStatement,resultSet);
        }
        return key;
    }

//    修改方法，返回受影响的行数
    public Integer updateType(String typeName, String typeId) {
        String sql = "update tb_note_type set typeName = ? where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        int row = BaseDao.executeUpdate(sql, params);
        return row;
    }
}
