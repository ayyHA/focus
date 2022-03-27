package com.focus.focus.message.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 本表意在维护每一篇讯息的交互信息，当用户需要时，可以通过虚拟列+JPQL/SQL获得。如用户总点赞数等。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "focus_message_public_data")
@Entity
@Builder
public class MessagePublicDataEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long messageId;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "reply_count")
    private Long replyCount;

    @Column(name = "retweet_count")
    private Long retweetCount;

//    @Column(name = "quote_count")
//    private Long quoteCount;

    @MapsId("messageId")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", referencedColumnName = "id", foreignKey
            = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MessageEntity messageEntity;

}
