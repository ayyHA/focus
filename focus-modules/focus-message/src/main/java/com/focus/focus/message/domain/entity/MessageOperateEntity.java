package com.focus.focus.message.domain.entity;

import com.focus.focus.api.enumerate.MessageOperateTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "focus_message_operate")
@Entity
@EqualsAndHashCode(of = "id")
public class MessageOperateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private MessageOperateId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id",referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MessageEntity messageEntity;

    @Column(name = "type",length = 10)
    @Enumerated(EnumType.STRING)
    private MessageOperateTypeEnum type;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"userId","messageId"})
    @Embeddable
    private static class MessageOperateId implements Serializable{
        private static final long serialVersionUID = 1L;

        @Column(name = "user_id",length = 32)
        private String userId;

        @Column(name = "message_id")
        private Long messageId;
    }
}
