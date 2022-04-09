package com.focus.focus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessagePublicDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long likeCount;
    private Long replyCount;
    private Long retweetCount;
}
