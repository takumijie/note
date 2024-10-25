package com.msb.note.util;
import com.alibaba.fastjson.JSON;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class jsonUtil {
    public static void toJSON(HttpServletResponse response, Object resultInFo){
        try {
            response.setContentType("application/json;charset=UTF-8");
//        得到字符输出流
            PrintWriter printWriter = response.getWriter();
//        通过fastjson方法，将resultInFo对象转换为json格式字符串
            String json = JSON.toJSONString(resultInFo);
//        通过输出流输出json字符串
            printWriter.write(json);
//        关闭资源
            printWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
