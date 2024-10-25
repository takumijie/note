package com.msb.note.vo;

import lombok.Getter;
import lombok.Setter;

/*
* 封装返回结果的类
*       状态码
*       成功为1失败为0
*       提示信息
*       返回对象集合（字符串，JavaBean，集合，map）
* */
@Getter
@Setter
public class ResultInFo<T> {
    private Integer code; //状态码
    private String msg; //提示信息
    private T result; //返回对象


}
