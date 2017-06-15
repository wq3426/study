package com.dhl.tools.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用消息返回类
 * Created by liuso on 2017/4/20.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInfo {

    // 操作是否成功
    boolean success;
    // 消息代码
    int code;
    // 消息内容
    String msg;
    // 数据
    Object data;
}
