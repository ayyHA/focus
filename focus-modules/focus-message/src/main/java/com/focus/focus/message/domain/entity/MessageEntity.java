package com.focus.focus.message.domain.entity;

import com.focus.focus.api.enumerate.MessageGrantType;
import com.focus.focus.api.enumerate.MessageTypeEnum;
import com.focus.focus.message.domain.listener.MessageAuditingListener;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "focus_message")
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"messagePublicDataEntity"})
@Builder
@EntityListeners(MessageAuditingListener.class)
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 文本内容
    @Column(name = "text", length = 600)
    private String text;

    // 图片内容
    @Column(name="images", length=300)
    private String images;

    @Column(name = "author_id", length = 32)
    private String authorId;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    // 原始文本/回复/转发
    @Column(name = "type",length = 10)
    @Enumerated(EnumType.STRING)
    private MessageTypeEnum type;

    // 若是回复，则指向另一条message
    @Column(name = "conversation_id")
    private Long conversationId;

    // 若是回复，指向原message的作者的ID
    @Column(name = "in_reply_to_author_id", length = 32)
    private String inReplyToAuthorId;

    // message带关键词
    @Column(name = "keywords", length = 32)
    private String keywords;

    // message权限
    @Column(name = "grant_type",length = 16)
    @Enumerated(EnumType.STRING)
    private MessageGrantType grantType;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "be_quoted_message",referencedColumnName = "id",
//            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private MessageEntity beQuotedMessage;
//    @Column(name = "be_quoted_message_id")
//    private Long beQuotedMessageId;

    @PrimaryKeyJoinColumn
    @OneToOne(fetch = FetchType.LAZY,mappedBy = "messageEntity",cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private MessagePublicDataEntity messagePublicDataEntity;
}
