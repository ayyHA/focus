package com.focus.focus.message.domain.entity;

import com.focus.focus.api.enumerate.MessageTypeEnum;
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
public class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", length = 140)
    private String text;

    @Column(name = "author_id", length = 32)
    private String authorId;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "type",length = 10)
    @Enumerated(EnumType.STRING)
    private MessageTypeEnum type;

    @Column(name = "conversation_id")
    private Long conversationId;

    @Column(name = "in_reply_to_author_id", length = 32)
    private String inReplyToAuthorId;

    @Column(name = "keywords", length = 32)
    private String keywords;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "be_quoted_message",referencedColumnName = "id",
//            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private MessageEntity beQuotedMessage;
//    @Column(name = "be_quoted_message_id")
//    private Long beQuotedMessageId;

    @PrimaryKeyJoinColumn
    @OneToOne(fetch = FetchType.LAZY,mappedBy = "messageEntity",cascade = CascadeType.ALL)
    private MessagePublicDataEntity messagePublicDataEntity;
}
