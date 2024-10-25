package com.msb.note.web;

import cn.hutool.core.util.StrUtil;
import com.msb.note.po.Note;
import com.msb.note.po.NoteType;
import com.msb.note.po.User;
import com.msb.note.service.NoteService;
import com.msb.note.service.NoteTypeService;
import com.msb.note.vo.ResultInFo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        设置高亮导航栏
        request.setAttribute("menu_page","note");
//         得到用户行为
        String actionName = request.getParameter("actionName");
//        判断用户行为
        if ("view".equals(actionName)){
//            进入发布云计页面
            noteView(request,response);
        } else if ("addOrUpdate".equals(actionName)) {
//             添加或修改云计
            addOrUpdate(request,response);
        } else if ("detail".equals(actionName)) {
            noteDetail(request,response);
        } else if ("deleteNote".equals(actionName)) {
            noteDelete(request,response);
        }
    }

    /**
     *             1. 接收参数 （noteId）
     *             2. 调用Service层删除方法，返回状态码 （1=成功，0=失败）
     *             3. 通过流将结果响应给ajax的回调函数 （输出字符串）
     * @param request
     * @param response
     */
    private void noteDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        1. 接收参数 （noteId）
        String noteId = request.getParameter("noteId");
//        2. 调用Service层删除方法，返回状态码 （1=成功，0=失败）
        Integer code = noteService.deleteNote(noteId);
//        3. 通过流将结果响应给ajax的回调函数 （输出字符串）
        response.getWriter().write(code+"");
        response.getWriter().close();
    }

    /**
     *             1. 接收参数 （noteId）
     *             2. 调用Service层的查询方法，返回Note对象
     *             3. 将Note对象设置到request请求域中
     *             4. 设置首页动态包含的页面值
     *             5. 请求转发跳转到index.jsp
     * @param request
     * @param response
     */
    private void noteDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        1. 接收参数 （noteId）
        String noteId = request.getParameter("noteId");
//        2. 调用Service层的查询方法，返回Note对象
        Note note = noteService.fiandNoteById(noteId);
//        3. 将Note对象设置到request请求域中
        request.setAttribute("note",note);
//        4. 设置首页动态包含的页面值
        request.setAttribute("changePage","note/detail.jsp");
//        5. 请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }

    /**
     * 1. 接收参数 （类型ID、标题、内容）
     *             2. 调用Service层方法，返回resultInfo对象
     *             3. 判断resultInfo的code值
     *                 如果code=1，表示成功
     *                     重定向跳转到首页 index
     *                 如果code=0，表示失败
     *                     将resultInfo对象设置到request作用域
     *                     请求转发跳转到note?actionName=view
     * @param request
     * @param response
     */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//           接收参数 （类型ID、标题、内容）
            String typeId = request.getParameter("typeId");
            String title = request.getParameter("title");
            String content = request.getParameter("content");
//            如果是修改操作，需要接收noteId
            String noteId = request.getParameter("noteId");
//            获取经纬度
            String lon = request.getParameter("lon");
            String lat = request.getParameter("lat");
//          调用Service层方法，返回resultInfo对象
            ResultInFo<Note> resultInFo = noteService.addOrUpdate(typeId,title,content,noteId,lon,lat);
//          判断resultInfo的code值
            if (resultInFo.getCode() == 1){
                response.sendRedirect("index");
            }else {
//                将resultInfo对象设置到request作用域
                request.setAttribute("resultInFo",resultInFo);
//                请求转发跳转到note?actionName=view
                String url ="note?actionName=view";
                if (!StrUtil.isBlank(noteId)){
                    url += "&noteId"+noteId;

                }
                request.getRequestDispatcher(url).forward(request,response);
            }
    }

    /**
     * 进入发布云计页面
     *后台：
     *         1. 从Session对象中获取用户对象
     *         2. 通过用户ID查询对应的类型列表
     *         3. 将类型列表设置到request请求域中
     *         4. 设置首页动态包含的页面值
     *         5. 请求转发跳转到index.jsp
     * @param request
     * @param response
     */
    private void noteView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //修改云记
        String noteId = request.getParameter("noteId");
        Note note = noteService.fiandNoteById(noteId);
        request.setAttribute("noteInfo",note);







        //        1. 从Session对象中获取用户对象
        User user = (User) request.getSession().getAttribute("user");
//        2. 通过用户ID查询对应的类型列表
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
//        3. 将类型列表设置到request请求域中
        request.setAttribute("typeList",typeList);
        //          请求转发跳转到index.jsp
        request.setAttribute("changePage","note/view.jsp");
//        请求转发到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
