package com.focus.focus.api.dto;

import com.focus.focus.api.enumerate.MessageGrantType;
import com.focus.focus.api.enumerate.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto implements Serializable {

    private final static long serialVersionUID = 1L;

    // 存放message的文本内容，含emoji表情
    private String text;
    // 存放系列图片的url，以","相隔
    private String images;
    // 存放作者的ID
    private String authorId;
    // 存放讯息发布的时间
    private Date createAt;
    // 存放讯息的类型
    private MessageTypeEnum type;
    // 为评论，则存放原讯息的ID
    private Long conversationId;
    // 为评论，存放原讯息作者Id
    private String inReplyToAuthorId;
    // 关键词
    private String keywords;
    // 授权
    private MessageGrantType grantType;

}
