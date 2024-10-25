package com.msb.note.filter;

import com.msb.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 非法访问拦截
 *  拦截的资源：
 *      所有的资源   /*
 *
 *      需要被放行的资源
 *          1. 指定页面，放行 （用户无需登录的即可访问的页面；例如：登录页面login.jsp、注册页面register.jsp等）
 *          2. 静态资源，放行 （存放在statics目录下的资源；例如：js、css、images等）
 *          3. 指定行为，放行 （用户无需登录即可执行的操作；例如：登录操作actionName=login等）
 *          4. 登录状态，放行 （判断session作用域中是否存在user对象；存在则放行，不存在，则拦截跳转到登录页面）
 *
 *  免登录（自动登录）
 *      通过Cookie和Session对象实现
 *
 *      什么时候使用免登录：
 *          当用户处于未登录状态，且去请求需要登录才能访问的资源时，调用自动登录功能
 *
 *      目的：
 *          让用户处于登录状态（自动调用登录方法）
 *
 *      实现：
 *          从Cookie对象中获取用户的姓名与密码，自动执行登录操作
 *              1. 获取Cookie数组  request.getCookies()
 *              2. 判断Cookie数组
 *              3. 遍历Cookie数组，获取指定的Cookie对象 （name为user的cookie对象）
 *              4. 得到对应的cookie对象的value （姓名与密码：userName-userPwd）
 *              5. 通过split()方法将value字符串分割成数组
 *              6. 从数组中分别得到对应的姓名与密码值
 *              7. 请求转发到登录操作  user?actionName=login&userName=姓名&userPwd=密码
 *              8. return
 *
 *     如果以上判断都不满足，则拦截跳转到登录页面
 *
 */
@WebFilter("/*")
public class LoginAccess implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        基于httpservlet
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        得到访问的路径
        String path = request.getRequestURI();//项目路径/资源路径
        if (path.contains("/login.jsp")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.contains("/static")) {
            filterChain.doFilter(request, response);
            return;
        }

//        放行行为
        if (path.contains("/user")) {
            String actionName = request.getParameter("actionName");
            if ("login".equals(actionName)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

//        登录状态
//        获取session作用域中的user对象
        User user = (User) request.getSession().getAttribute("user");
//        判断user对象是否为空
        if (user != null){
            filterChain.doFilter(request, response);
            return;
        }

//        1. 获取Cookie数组  request.getCookies()
        Cookie[] cookies = request.getCookies();
//        2. 判断Cookie数组
        if (cookies != null && cookies.length>0){
            for (Cookie cookie:cookies){
//                3. 遍历Cookie数组，获取指定的Cookie对象 （name为user的cookie对象）
                if ("user".equals(cookie.getName())){
//                    4. 得到对应的cookie对象的value （姓名与密码：userName-userPwd）
                    String value = cookie.getValue();
//                    5. 通过split()方法将value字符串分割成数组
                    String[] val = value.split("-");
//                    6. 从数组中分别得到对应的姓名与密码值
                    String userName = val[0];
                    String userPwd = val[1];
//                    7. 请求转发到登录操作  user?actionName=login&userName=姓名&userPwd=密码
                    String url = "http://localhost:8080/note/user?actionName=login&userName="+userName+"&userPwd="+userPwd+"&rem=1";
//                    request.getRequestDispatcher(url).forward(request,response);
                    response.sendRedirect(url);
//                    8. return
                    return;
                }
            }
        }

        response.sendRedirect("login.jsp");
    }

    @Override
    public void destroy() {

    }
}

