package com.msb.note.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Note {
    private Integer noteId;//云计id
    private String title;// 云计标题
    private String content;// 云计内容
    private Integer typeId;// 云计类型Id
    private Date pubTime;//发布时间
    private Float lon; // 经度
    private Float lat; // 纬度

    private String typeName;
}
