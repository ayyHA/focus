package com.focus.focus.api.util;

import lombok.Getter;

@Getter
public enum ResponseCode {
    /* 偶数正确奇数错 */
    // USER
    AVATAR_UPLOAD_ERROR(2001,"用户头像上传失败"),
    AVATAR_UPLOAD_SUCCESS(2002,"用户头像上传成功"),
    USER_UPLOAD_BACKGROUND_ERROR(2007,"用户背景图上传失败"),
    USER_UPLOAD_BACKGROUND_SUCCESS(2008,"用户背景图上传成功"),
    USER_UPDATE_DETAILS_ERROR(2009,"用户个人信息更新失败"),
    USER_UPDATE_DETAILS_SUCCESS(2010,"用户个人信息更新成功"),
    // MESSAGE
    MESSAGE_UPLOAD_MULTI_IMAGES_ERROR(2003,"讯息图像上传失败"),
    MESSAGE_UPLOAD_MULTI_IMAGES_SUCCESS(2004,"讯息图像上传成功"),
    MESSAGE_PUBLISH_ERROR(2005,"讯息发布失败"),
    MESSAGE_PUBLISH_SUCCESS(2006,"讯息发布成功"),
    MESSAGE_SHOW_ERROR(2011,"讯息获取失败"),
    MESSAGE_SHOW_SUCCESS(2012,"讯息获取成功"),
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
