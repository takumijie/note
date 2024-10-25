package com.msb.note.web;

import com.msb.note.po.Note;
import com.msb.note.po.User;
import com.msb.note.service.NoteService;
import com.msb.note.util.Page;
import com.msb.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        设置首页高亮
        request.setAttribute("menu_page","index");
        //        得到用户行为
        String actionName = request.getParameter("actionName");
//        将用户行为设置到request作用域中(分页导航需要获取)
        request.setAttribute("action",actionName);
//        得到用户行为判断是什么类型的查询
        if ("searchTitle".equals(actionName)){//标题查询
            String title = request.getParameter("title");
            request.setAttribute("title",title);
            noteList(request,response,title,null,null);
        }else if ("searchDate".equals(actionName)){//日期查询
            //得到查询条件
            String date = request.getParameter("date");
            request.setAttribute("date",date);
            noteList(request,response,null,date,null);
        }else if ("searchType".equals(actionName)){//类型查询
            //得到查询条件
            String typeId = request.getParameter("typeId");
            request.setAttribute("typeId",typeId);
            noteList(request,response,null,null,typeId);
        }else {
            noteList(request,response,null,null,null);
        }

//        设置首页动态包含
        request.setAttribute("changePage","note/list.jsp");
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }

    /**
     * 分页查询云计列表
 *             1. 接收参数 （当前页、每页显示的数量）
 *             2. 获取Session作用域中的user对象
 *             3. 调用Service层查询方法，返回Page对象
 *             4. 将page对象设置到request作用域中
     * @param request
     * @param response
     */
    private void noteList(HttpServletRequest request, HttpServletResponse response ,String title,String date,String typeId) {
//        1. 接收参数 （当前页、每页显示的数量）
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");
//        2. 获取Session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");
//        3. 调用Service层查询方法，返回Page对象
        Page<Note> page = new NoteService().findNoteListByPage(pageNum,pageSize,user.getUserId(),title,date,typeId);
//        4. 将page对象设置到request作用域中
        request.setAttribute("page",page);
//        通过日期查询当前登录用户下的云记数量
        List<NoteVo> dateInfo = new NoteService().findNoteCountByDate(user.getUserId());
//        将dateInfo存到request作用域中
        request.getSession().setAttribute("dateInfo",dateInfo);
//        通过日期查询当前登录用户下的云记数量
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
//        将dateInfo存到request作用域中
        request.getSession().setAttribute("typeInfo",typeInfo);
    }
}
