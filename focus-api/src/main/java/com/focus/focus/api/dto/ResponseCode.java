package com.focus.focus.api.dto;

import lombok.Getter;

@Getter
public enum ResponseCode {
    /* 偶数正确奇数错 */
    AVATAR_UPLOAD_ERROR(2001,"头像上传错误"),
    AVATAR_UPLOAD_SUCCESS(2002,"头像上传成功"),
    /* ...Repository错误 */
    USER_REPOSITORY_ERROR(3001,"用户DAO层错误"),
    MESSAGE_REPOSITORY_ERROR(3002,"讯息DAO层错误"),
    PUSH_REPOSITORY_ERROR(3003,"推送DAO层错误"),
    SEARCH_REPOSITORY_ERROR(3004,"搜索DAO层错误"),
    PAY_REPOSITORY_ERROR(3005,"支付DAO层错误"),
    CHAT_REPOSITORY_ERROR(3006,"聊天DAO层错误");

    private final int code;
    private final String msg;

    private ResponseCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
}
