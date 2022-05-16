package com.focus.redis.common.util;

public class RedisKeyUtil {
    // 点赞的Key
    public static final String HASH_KEY_LIKE = "HASH_MESSAGE_LIKE";
    // 点赞数量的key
    public static final String HASH_KEY_LIKE_COUNT = "HASH_MESSAGE_LIKE_COUNT";
    // 聊天集合的Key(需拼接)
    public static final String ZSET_KEY_CHAT_ZSET = "ChatZSet:";
    // 历史聊天记录的Key(需拼接)
    public static final String LIST_KEY_CHAT_HISTORY = "ChatHistory:";

    // 形如1::4000151321..此形式
    public static String linkedId(Long messageId,String userId){
        StringBuilder builder = new StringBuilder();
        builder.append(messageId);
        builder.append("::");
        builder.append(userId);
        return builder.toString();
    }
}
