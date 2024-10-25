package com.msb.note.web;

import com.alibaba.fastjson.JSON;
import com.msb.note.po.NoteType;
import com.msb.note.po.User;
import com.msb.note.service.NoteTypeService;
import com.msb.note.util.jsonUtil;
import com.msb.note.vo.ResultInFo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet {
    private NoteTypeService typeService = new NoteTypeService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("menu_page","type");
        //        得到用户行为
        String actionName =request.getParameter("actionName");
//        判断用户行为
        if ("list".equals(actionName)){
//           查询类型列表
            typeList(request,response);
        }else if ("delete".equals(actionName)){
//            删除类型
            deleteType(request,response);
        } else if ("addOrUpdate".equals(actionName)) {
//            添加和修改类型
            addOrUpdate(request,response);
        }

    }

    /**
     * 1. 接收参数 （类型名称、类型ID）
     *             2. 获取Session作用域中的user对象，得到用户ID
     *             3. 调用Service层的更新方法，返回ResultInfo对象
     *             4. 将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数
     * @param request
     * @param response
     */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) {
//        1. 接收参数 （类型名称、类型ID）
        String typeId = request.getParameter("typeId");
        String typeName = request.getParameter("typeName");
//        2.获取用户对象需要使用用户id
        User user = (User) request.getSession().getAttribute("user");
        Integer userId = user.getUserId();
//        3. 调用Service层的更新方法，返回ResultInfo对象
        ResultInFo<Integer> resultInFo = typeService.addOrUpdate(typeName,userId,typeId);
//        4. 将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数
        jsonUtil.toJSON(response,resultInFo);
    }

    /**
     *1. 接收参数（类型ID）
     *             2. 调用Service的更新操作，返回ResultInfo对象
     *             3. 将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
     * @param request
     * @param response
     */
    private void deleteType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String typeId = request.getParameter("typeId");
//        调用Service的更新操作，返回ResultInfo对象
        ResultInFo<NoteType> resultInFo = typeService.deleteType(typeId);
//        将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
        jsonUtil.toJSON(response,resultInFo);
//
    }

//    查询类型列表
    /**
     *Web层：
     *             1. 获取Session作用域设置的user对象
     *             2. 调用Service层的查询方法，查询当前登录用户的类型集合，返回集合
     *             3. 将类型列表设置到request请求域中
     *             4. 设置首页动态包含的页面值
     *             5. 请求转发跳转到index.jsp页面
     * @param request
     * @param response
     */
    private void typeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //        1. 获取Session作用域设置的user对象
        User user = (User) request.getSession().getAttribute("user");
//        2. 调用Service层的查询方法，查询当前登录用户的类型集合，返回集合
        List<NoteType> typeList = typeService.findTypeList(user.getUserId());
//        3. 将类型列表设置到request请求域中
        request.setAttribute("typeList",typeList);
//        4. 设置首页动态包含的页面值
        request.setAttribute("changePage","type/list.jsp");
//        5. 请求转发跳转到index.jsp页面
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }
}
