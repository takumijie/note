package com.msb.note.service;

import cn.hutool.json.JSONUtil;
import com.msb.note.po.Note;
import com.msb.note.po.User;
import com.msb.note.util.jsonUtil;
import com.msb.note.vo.ResultInFo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class reportServlet extends HttpServlet {
    private NoteService noteServce = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //        设置高亮导航栏
        request.setAttribute("menu_page","report");
        String actionName = request.getParameter("actionName");
        if ("info".equals(actionName)){
            reportInfo(request,response);
        } else if ("month".equals(actionName)) {
//            通过月份查询云记数量
            queryNoteCountByMonth(request,response);
        } else if ("location".equals(actionName)) {
//            查询用户发布云记时的坐标
            queryNoteLonAndLat(request,response);
        }

    }

    /**
     * 查询用户发布云记时的坐标
     * @param request
     * @param response
     */
    private void queryNoteLonAndLat(HttpServletRequest request, HttpServletResponse response) {
        //从session中获取用户对象
        User user = (User) request.getSession().getAttribute("user");
        //        调用service层的查询方法返回resultInfo
        ResultInFo<List<Note>> resultInFo = noteServce.queryNoteLonAndLat(user.getUserId());
        //        转换为json传送给前端
        jsonUtil.toJSON(response,resultInFo);
    }

    /**
     * 通过月份查询云记数量
     * @param request
     * @param response
     */
    private void queryNoteCountByMonth(HttpServletRequest request, HttpServletResponse response) {
            //从session中获取用户对象
        User user = (User) request.getSession().getAttribute("user");
//        调用service层的查询方法
        ResultInFo<Map<String,Object>> resultInFo = noteServce.queryNoteCountByMonth(user.getUserId());
//        转换为json传送给前端
        jsonUtil.toJSON(response,resultInFo);
    }

    /**
     *进入报表页面
     * @param request
     * @param response
     */
    private void reportInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        设置首页包含页面值
        request.setAttribute("changePage","report/info.jsp");
//        请求转发跳转到index
        request.getRequestDispatcher("index.jsp").forward(request,response);
//
    }
}
