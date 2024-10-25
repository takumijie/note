package com.msb.note.filter;


import cn.hutool.core.util.StrUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebFilter("/*")
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        处理post请求，get不受影响
        request.setCharacterEncoding("UTF-8");
//        如果是get请求判断服务器版本
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)){
//            得到服务器版本
            String ServerInfo = request.getServletContext().getServerInfo();
//            截取字符串获得具体版本号
            String version = ServerInfo.substring(ServerInfo.lastIndexOf("/")+1,ServerInfo.indexOf("."));
//            判断服务器版本是否在7以下
            if (version != null && Integer.parseInt(version) < 8){
//                tomcat7及以下版本
                MyWapper myRequest = new MyWapper(request);
                filterChain.doFilter(myRequest,response);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }


//    定义内部类
    class MyWapper extends HttpServletRequestWrapper{
        //              定义成员变量HttpServletRequest对象（提升构造器中request对象的作用域）
        private HttpServletRequest request;

        public MyWapper(HttpServletRequest request) {
            super(request);
            this.request=request;
        }
        //          重写方法处理乱码问题
        @Override
        public String getParameter(String name) {
//                获取乱码的参数
            String value = request.getParameter(name);
//                判断参数值是否为空
            if (StrUtil.isBlank(value)){
                return value;
            }
            try {
//                    使用new String处理乱码
                value = new String(value.getBytes("ISO-8859-1"),"UTF-8");

            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return value;
        }
    }


    @Override
    public void destroy() {

    }
}
