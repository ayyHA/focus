package com.focus.redis.common.util;

public class RedisKeyUtil {
    // 点赞的Key
    public static final String HASH_KEY_LIKE = "HASH_MESSAGE_LIKE";
    // 点赞数量的key
    public static final String HASH_KEY_LIKE_COUNT = "HASH_MESSAGE_LIKE_COUNT";

    // 形如1::4000151321..此形式
    public static String linkedId(Long messageId,String userId){
        StringBuilder builder = new StringBuilder();
        builder.append(messageId);
        builder.append("::");
        builder.append(userId);
        return builder.toString();
    }
}
