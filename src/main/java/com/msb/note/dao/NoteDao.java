package com.msb.note.dao;

import cn.hutool.core.util.StrUtil;
import com.msb.note.po.Note;
import com.msb.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    /**
     *通过日期查询当前登录用户下云记数量集合
     * @param userId
     * @return
     */
    public static List<NoteVo> findNoteCountByDate(Integer userId) {
//        定义sql语句
        String sql = "SELECT count(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName FROM tb_note n " +
                " INNER JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY DATE_FORMAT(pubTime,'%Y年%m月')" +
                " ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC ";
        //        设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
//        调用baseDao
        List<NoteVo> list = BaseDao.queryRows(sql,params, NoteVo.class);
        return list;
    }

    /**
     *通过类型查询当前登录用户下云记数量集合
     * @param userId
     * @return
     */
    public static List<NoteVo> findNoteCountByType(Integer userId) {
        // 定义SQL语句
        String sql = "SELECT count(noteId) noteCount, t.typeId, typeName groupName FROM tb_note n " +
                " RIGHT JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY t.typeId ORDER BY COUNT(noteId) DESC ";
        //        设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //        调用baseDao
        List<NoteVo> list = BaseDao.queryRows(sql,params, NoteVo.class);
        return list;
    }

    /**
     *添加云记，返回受影响的行数
     * @param note
     * @return
     */
    public int addOrUpdate(Note note) {
    //定义sql语句
        String sql = "";
//        设置参数
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());
//        判断noteId是否为空
        if (note.getNoteId() == null){//添加操作
            sql = "insert into tb_note (typeId, title, content, pubTime, lon, lat) values (?,?,?,now(),?,?)";
            params.add(note.getLon());
            params.add(note.getLat());
        } else {//修改参数
             sql = "update tb_note set typeId = ?,title = ? ,content = ? where noteId = ?";
            params.add(note.getNoteId());
        }

//        调用baseDao更新方法
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

    /**
     * 查询当前登录用户的云记数量，返回总记录数
     * @param userId
     * @return
     */
    public long findNoteCount(Integer userId , String title,String date,String typeId) {
        String sql = "select count(1) from tb_note n inner join tb_note_type t on t.typeId = n.typeId where userId = ?";
//        设置参数
//        title = "测试";
        List<Object> params = new ArrayList<>();
        params.add(userId);
//        判断条件查询参数是否为空
        if (!StrUtil.isBlank(title)){//标题查询
//            查询参数不为空就是标题搜索,拼接sql语句并设置参数
            sql += " and title like concat('%',?,'%') ";
//            设置sql所需参数
            params.add(title);
        } else if (!StrUtil.isBlank(date)) {//日期查询
//            查询参数不为空就是标题搜索,拼接sql语句并设置参数
            sql += " and date_format(pubTime,'%Y年%m月')= ? ";
//            设置sql所需参数
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)) {//类型查询
//            查询参数不为空就是标题搜索,拼接sql语句并设置参数
            sql += " and n.typeId = ? ";
//            设置sql所需参数
            params.add(typeId);
        }
//        调用baseDao
        long count = (long) BaseDao.findSingleValue(sql,params);
        System.out.println(count);
        return count;
    }

    /**
     * 查询当前登录用户下当前页的数据列表，返回note集合
     * @param userId
     * @param index
     * @param pageSize
     * @return
     */
    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,String title,String date,String typeId) {
//        定义sql
        String sql = "select noteId,title,pubTime from tb_note n inner join tb_note_type t on t.typeId = n.typeId where userId = ?";
//        设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //        判断条件查询参数是否为空
        if (!StrUtil.isBlank(title)){
//            查询参数不为空就是标题搜索,拼接sql语句并设置参数
            sql += " and title like concat('%',?,'%') ";
//            设置sql所需参数
            params.add(title);
        }else if (!StrUtil.isBlank(date)) {
//            查询参数不为空就是标题搜索,拼接sql语句并设置参数
            sql += " and date_format(pubTime,'%Y年%m月')= ? ";
//            设置sql所需参数
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)) {//类型查询
//            查询参数不为空就是标题搜索,拼接sql语句并设置参数
            sql += " and n.typeId = ? ";
//            设置sql所需参数
            params.add(typeId);
        }
        sql += " order by pubTime desc limit ?,? ";
        params.add(index);
        params.add(pageSize);
//        调用baseDao
        List<Note> noteList = BaseDao.queryRows(sql,params,Note.class);
        return noteList;
    }

    //  通过noteId查询note对象
    public Note findNoteById(String noteId) {
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n " +
                " inner join tb_note_type t on n.typeId=t.typeId where noteId = ?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);

        Note note = (Note) BaseDao.queryRow(sql,params,Note.class);

        return note;
    }

    /**
     * 通过id删除云记返回受影响行数
     * @param noteId
     * @return
     */
    public int deleteNoteById(String noteId) {
        String sql = "delete from tb_note where noteId= ?";

        List<Object> params = new ArrayList<>();
        params.add(noteId);

        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

    /**
     * 通过用户id查询云记列表
     * @param userId
     * @return
     */
    public List<Note> queryNoteList(Integer userId) {
        String sql = "select lon, lat from  tb_note n inner join tb_note_type t on n.typeId = t.typeId where userId = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        List<Note> list = BaseDao.queryRows(sql,params,Note.class);
        return list;
    }
}
