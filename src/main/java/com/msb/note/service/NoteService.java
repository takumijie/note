package com.msb.note.service;

import cn.hutool.core.util.StrUtil;
import com.msb.note.dao.NoteDao;
import com.msb.note.po.Note;
import com.msb.note.util.Page;
import com.msb.note.vo.NoteVo;
import com.msb.note.vo.ResultInFo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    NoteDao noteDao = new NoteDao();

    /**
*             1. 设置回显对象 Note对象
*             2. 参数的非空判断
*                 如果为空，code=0，msg=xxx，result=note对象，返回resultInfo对象
*             2. 调用Dao层，添加云记记录，返回受影响的行数
*             3. 判断受影响的行数
*                 如果大于0，code=1
*                 如果不大于0，code=0，msg=xxx，result=note对象
*             4. 返回resultInfo对象
     * @param typeId
     * @param title
     * @param content
     * @return
     */
    public ResultInFo<Note> addOrUpdate(String typeId, String title, String content,String noteId,String lon,String lat) {
        ResultInFo<Note> resultInFo = new ResultInFo<>();


//        参数的非空判断
        if (StrUtil.isBlank(typeId)){
            resultInFo.setCode(0);
            resultInFo.setMsg("请选择云计类型");
            return resultInFo;
        }
        if (StrUtil.isBlank(title)){
            resultInFo.setCode(0);
            resultInFo.setMsg("云计标题不能为空");
            return resultInFo;
        }
        if (StrUtil.isBlank(content)){
            resultInFo.setCode(0);
            resultInFo.setMsg("云计内容不能为空");
            return resultInFo;
        }
//        设置经纬度默认值
        if (lon==null || lat == null){
            lon = "104.06";
            lat = "30.67";
        }

//         设置回显对象 Note对象
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTypeId(Integer.valueOf(typeId));
        note.setLon(Float.valueOf(lon));
        note.setLat(Float.valueOf(lat));

        if (!StrUtil.isBlank(noteId)){
            note.setNoteId(Integer.valueOf(noteId));
        }
        resultInFo.setResult(note);


//        调用Dao层，添加云记记录，返回受影响的行数
        int row = noteDao.addOrUpdate(note);
//        判断受影响的行数
        if (row>0){
            resultInFo.setCode(1);
        }else {
            resultInFo.setCode(0);
            resultInFo.setMsg("更新失败");
            resultInFo.setResult(note);
        }
        return resultInFo;
    }

    /**
*             1. 参数的非空校验
*                 如果分页参数为空，则设置默认值
*             2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
*             3. 判断总记录数是否大于0
*             4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
*             5. 查询当前登录用户下当前页的数据列表，返回note集合
*             6. 将note集合设置到page对象中
*             7. 返回Page对象
     * @param pageNumStr
     * @param pageSizeStr
     * @param userId
     * @param title
     * @return
     */
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId ,String title,String date,String typeId) {
//        设置分页参数默认值
        Integer pageNum = 1;//默认第一页
        Integer pageSize = 5;//默认每页10条
//         参数非空校验 (如果参数不为空则设置参数)
        if (!StrUtil.isBlank(pageNumStr)){
//            当前页
            pageNum = Integer.parseInt(pageNumStr);
        }
        if (!StrUtil.isBlank(pageSizeStr)){
//            设置每页显示的数量
            pageSize = Integer.parseInt(pageSizeStr);
        }
//        2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
            long count = noteDao.findNoteCount(userId,title,date,typeId);
//        3. 判断总记录数是否大于0
        if (count<1){
            return null;
        }
//        4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
        Page<Note> page = new Page<>(pageNum,pageSize,count);
//        得到数据库中分页查询开始的下标
        Integer index = (pageNum - 1) * pageSize;
//        5. 查询当前登录用户下当前页的数据列表，返回note集合
        List<Note> noteList = noteDao.findNoteListByPage(userId,index,pageSize,title,date,typeId);
//        6. 将note集合设置到page对象中
        page.setDataList(noteList);
//        7. 返回Page对象
        return page;
    }

    /**
     *查询当前登录用户下云记数量集合
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        return NoteDao.findNoteCountByDate(userId);
    }

    /**
     *查询当前登录用户下云记数量集合
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        return NoteDao.findNoteCountByType(userId);
    }

    /**
     * 查询云记详情
     *             1. 参数的非空判断
     *             2. 调用Dao层的查询，通过noteId查询note对象
     *             3. 返回note对象
     * @param noteId
     * @return
     */
    public Note fiandNoteById(String noteId) {
//        1. 参数的非空判断
        if (StrUtil.isBlank(noteId)){
            return null;
        }
//        2. 调用Dao层的查询，通过noteId查询note对象
        Note note = noteDao.findNoteById(noteId);
//        3. 返回note对象
        return note;
    }

    /**
     *             1. 判断参数
     *             2. 调用Dao层的更新方法，返回受影响的行数
     *             3. 判断受影响的行数是否大于0
     *                 如果大于0，返回1；否则返回0
     * @param noteId
     * @return
     */
    public Integer deleteNote(String noteId) {
//        1. 判断参数
        if (StrUtil.isBlank(noteId)){
            return 0;
        }
//        2. 调用Dao层的更新方法，返回受影响的行数
        int row = noteDao.deleteNoteById(noteId);
//        3. 判断受影响的行数是否大于0
//           如果大于0，返回1；否则返回0
        if (row>0){
            return 1;
        }
        return 0;
    }

    /**
     * 通过月份查询用户云记数量
     * @param userId
     * @return
     */
    public ResultInFo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        ResultInFo<Map<String, Object>> resultInFo = new ResultInFo<>();
//        通过月份分类查询云记数量
        List<NoteVo> noteVos = NoteDao.findNoteCountByDate(userId);
//         判断集合是否存在
        if (noteVos != null && noteVos.size()>0){
            //得到月份
            List<String> monthList = new ArrayList<>();
            //得到云记数量
            List<Integer> noteCount = new ArrayList<>();
            //遍历月份分组
            for (NoteVo noteVo: noteVos){
                monthList.add(noteVo.getGroupName());
                noteCount.add((int) noteVo.getNoteCount());
            }
            //准备map对象，封装对应的云记数量
            Map<String, Object> map = new HashMap<>();
            map.put("monthArray",monthList);
            map.put("dataArray",noteCount);
            //将对象设置到ResultInfo对象中
            resultInFo.setCode(1);
            resultInFo.setResult(map);
        }


        return resultInFo;
    }

    /**
     * 查询用户发布云记时的坐标
     * @param userId
     * @return
     */
    public ResultInFo<List<Note>> queryNoteLonAndLat(Integer userId) {
        ResultInFo<List<Note>> resultInFo = new ResultInFo<>();
        //通过用户id查询对应的坐标
        List<Note> noteList = noteDao.queryNoteList(userId);
        //判断是否为空
        if (noteList != null && noteList.size() > 0){
            resultInFo.setCode(1);
            resultInFo.setResult(noteList);
        }
        return resultInFo;
    }
}
