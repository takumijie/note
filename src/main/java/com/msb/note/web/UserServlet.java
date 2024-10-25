package com.msb.note.web;

import com.msb.note.po.User;
import com.msb.note.service.UserService;
import com.msb.note.vo.ResultInFo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
/*
* 用户登录
         1. 获取参数 （姓名、密码）
         2. 调用Service层的方法，返回ResultInfo对象
         3. 判断是否登录成功
             如果失败
                 将resultInfo对象设置到request作用域中
                 请求转发跳转到登录页面
             如果成功
                将用户信息设置到session作用域中
                判断用户是否选择记住密码（rem的值是1）
                    如果是，将用户姓名与密码存到cookie中，设置失效时间，并响应给客户端
                    如果否，清空原有的cookie对象
                重定向跳转到index页面
* */
@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //设置首页高亮
        request.setAttribute("menu_page","user");

        //接收用户行为
        String actionName = request.getParameter("actionName");
            //用户登录
        if ("login".equals(actionName)){
            userLogin(request,response);
        }else if ("logout".equals(actionName)){
            //用户退出
            userLogout(request,response);
        } else if ("userCenter".equals(actionName)) {
            //用户个人中心
            userCenter(request,response);
        }else if ("userHead".equals(actionName)){
//            加载头像
            userHead(request,response);
        } else if ("checkNick".equals(actionName)) {
//            验证昵称唯一性
            checkNick(request,response);
        }else if ("updateUser".equals(actionName)){
//            修改用户信息
            updateUser(request,response);
        }
    }
//修改用户信息
    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
            ResultInFo<User> resultInFo = userService.updateUser(request);
//        2. 将resultInfo对象存到request作用域中
            request.setAttribute("resultInfo",resultInFo);
//        3. 请求转发跳转到个人中心页面 （user?actionName=userCenter）
            request.getRequestDispatcher("user?actionName=userCenter").forward(request,response);
    }

    //    验证昵称唯一性
    private void checkNick(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        1. 获取参数（昵称）
        String nick = request.getParameter("nick");
//        2. 从session作用域获取用户对象，得到用户ID
        User user = (User) request.getSession().getAttribute("user");
//        3. 调用Service层的方法，得到返回的结果
        Integer code = userService.checkNick(user.getUserId(),nick);
//        4. 通过字符输出流将结果响应给前台的ajax的回调函数
        response.getWriter().write(code+"");
//        5. 关闭资源
        response.getWriter().close();
    }


    //加载图片
    private void userHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        获得参数
        String head = request.getParameter("imageName");
//        获得图片存放路径
        String  realPath = request.getServletContext().getRealPath("/WEB-INF/upload");
//        通过图片完整路径得到file对象
        File file = new File(realPath+"/"+head);
        String pic = head.substring(head.lastIndexOf(".")+1);
//        通过不同的图片后缀，设置不同的响应类型
        if ("png".equalsIgnoreCase(pic)){
            response.setContentType("image/png");
        }else if ("jpg".equalsIgnoreCase(pic) || "jpeg".equalsIgnoreCase(pic)){
            response.setContentType("image/jpeg");
        } else if ("gif".equalsIgnoreCase(pic)) {
            response.setContentType("image/git");
        }
//        通过FileUtils方法将图片传给客户端
        FileUtils.copyFile(file,response.getOutputStream());

    }

    //用户个人中心
    private void userCenter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        设置动态包含的页面
        request.setAttribute("changePage","user/info.jsp");
//        请求转发跳转到index
        request.getRequestDispatcher("index.jsp").forward(request,response);
    }

    //退出方法
    private void userLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        销毁cookie对象
        request.getSession().invalidate();
//        删除cookie对象
        Cookie cookie = new Cookie("user",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
//      重定向
        response.sendRedirect("login.jsp");
    }


    //    登录方法
    private void userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        获取参数 （姓名、密码）
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");
//        调用Service层的方法，返回ResultInfo对象
        ResultInFo<User> resultInFo = new UserService().userLogin(userName, userPwd);
//        判断是否登录成功
        if (resultInFo.getCode()==1){
//            将用户信息设置到session作用域中
            HttpSession session = request.getSession();
            session.setAttribute("user",resultInFo.getResult());
//            判断用户是否选择记住密码（rem的值是1）
            if ("1".equals(request.getParameter("rem"))){
                Cookie cookie = new Cookie("user",userName+"-"+userPwd);
                cookie.setMaxAge(60*60*24*3);
                response.addCookie(cookie);
            }else {
                Cookie cookie = new Cookie("user",null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            response.sendRedirect("index");
        }else {
            /*如果失败
             将resultInfo对象设置到request作用域中
             请求转发跳转到登录页面*/
            request.setAttribute("resultInFo",resultInFo);
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }
}
