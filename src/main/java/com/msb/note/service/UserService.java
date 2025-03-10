package com.msb.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.msb.note.dao.UserDao;
import com.msb.note.po.User;
import com.msb.note.vo.ResultInFo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;

public class UserService {
    /*
    * 用户登录
         1. 判断参数是否为空
             如果为空
                 设置ResultInfo对象的状态码和提示信息
                 返回resultInfo对象
         2. 如果不为空，通过用户名查询用户对象
         3. 判断用户对象是否为空
             如果为空
                 设置ResultInfo对象的状态码和提示信息
                 返回resultInfo对象
         4. 如果用户对象不为空，将数据库中查询到的用户对象的密码与前台传递的密码作比较 （将密码加密后再比较）
             如果密码不正确
                 设置ResultInfo对象的状态码和提示信息
                 返回resultInfo对象
         5. 如果密码正确
            设置ResultInfo对象的状态码和提示信息
         6. 返回resultInfo对象
    * */

    private UserDao userDao = new UserDao();
    public ResultInFo<User> userLogin(String userName, String userPwd){
        ResultInFo<User> resultInFo = new ResultInFo<>();
        User u = new User();
        u.setUpwd(userPwd);
        u.setUname(userName);
        resultInFo.setResult(u);


        //        判断参数是否为空
        if (StrUtil.isBlank(userName) || StrUtil.isBlank(userPwd)){
            resultInFo.setCode(0);
            resultInFo.setMsg("用户姓名或密码不能为空");
            return resultInFo;
        }

        User user = userDao.queryUserByName(userName);
        if (user == null){
            resultInFo.setCode(0);
            resultInFo.setMsg("账号或密码不存在");
            return resultInFo;
        }

        userPwd = DigestUtil.md5Hex(userPwd);
        if (!userPwd.equals(user.getUpwd())){
            resultInFo.setCode(0);
            resultInFo.setMsg("用户密码不正确");
            return resultInFo;
        }

        resultInFo.setCode(1);
        resultInFo.setResult(user);
        return resultInFo;
    }

        //查询昵称的唯一性
    public Integer checkNick(Integer userId, String nick) {
        //        1. 判断昵称是否为空
        //        如果为空，返回"0"
        if (StrUtil.isBlank(nick)){
            return 0;
        }
        //        2. 调用Dao层，通过用户ID和昵称查询用户对象
        User user = userDao.queryUserByNickAndUserId(nick,userId);
        //        3. 判断用户对象存在
        //        存在，返回"0"
        //        不存在，返回"1"
        if (user != null){
            return 0;
        }
        return 1;
    }

    /**
     * Service层：
     *             1. 获取参数（昵称、心情）
     *             2. 参数的非空校验（判断必填参数非空）
     *                 如果昵称为空，将状态码和错误信息设置resultInfo对象中，返回resultInfo对象
     *             3. 从session作用域中获取用户对象（获取用户对象中默认的头像）
     *             4. 实现上上传文件
     *                 1. 获取Part对象 request.getPart("name"); name代表的是file文件域的name属性值
     *                 2. 通过Part对象获取上传文件的文件名
     *                 3. 判断文件名是否为空
     *                 4. 获取文件存放的路径  WEB-INF/upload/目录中
     *                 5. 上传文件到指定目录
     *             5. 更新用户头像 （将原本用户对象中的默认头像设置为上传的文件名）
     *             6. 调用Dao层的更新方法，返回受影响的行数
     *             7. 判断受影响的行数
     *                 如果大于0，则修改成功；否则修改失败
     *             8. 返回resultInfo对象
     * @param request
     * @return
     */
    public ResultInFo<User> updateUser(HttpServletRequest request) {
        ResultInFo<User> resultInFo = new ResultInFo<>();
//        1. 获取参数（昵称、心情）
        String nick = request.getParameter("nick");
        String mood = request.getParameter("mood");
//        2. 参数的非空校验（判断必填参数非空）
        if (StrUtil.isBlank(nick)){
            resultInFo.setCode(0);
            resultInFo.setMsg("用户昵称不能为空");
//          如果昵称为空，将状态码和错误信息设置resultInfo对象中，返回resultInfo对象
            return resultInFo;
        }
//        3. 从session作用域中获取用户对象（获取用户对象中默认的头像）
        User user = (User) request.getSession().getAttribute("user");
        //        设置修改的昵称和头像
        user.setNick(nick);
        user.setMood(mood);
//        4. 实现上上传文件
        try {
            //            1. 获取Part对象 request.getPart("name"); name代表的是file文件域的name属性值
            Part part = request.getPart("img");
            //            2. 通过Part对象获取上传文件的文件名
            String headr = part.getHeader("Content-Disposition");
            //          获取具体的请求头对应的值
            String str = headr.substring(headr.lastIndexOf("=")+2);
//            获取上传的文件名
            String fileName = str.substring(0,str.length()-1);
            //            3. 判断文件名是否为空
            if (!StrUtil.isBlank(fileName)){
//              如果用户上传了头像，则更新用户对象中的头像
                user.setHead(fileName);
                //            4. 获取文件存放的路径  WEB-INF/upload/目录中
                String filePath = request.getServletContext().getRealPath("/WEB-INF/upload/");
                //            5. 上传文件到指定目录
                part.write(filePath+"/"+fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
//        6. 调用Dao层的更新方法，返回受影响的行数
        int row = userDao.updateUser(user);
//        7. 判断受影响的行数,如果大于0，则修改成功；否则修改失败
        if (row>0){
            resultInFo.setCode(1);
//            更新session中的user对象
            request.getSession().setAttribute("user",user);
        }else {
            resultInFo.setCode(0);
            resultInFo.setMsg("更新失败");
        }
        return resultInFo;
    }
}
