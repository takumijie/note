package com.msb.note.po;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteType {
    private Integer typeId; //类型id
    private String typeName; // 类型名
    private Integer userId; // 用户id
}
