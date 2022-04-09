package com.focus.focus.message.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "focus_message_like")
@Entity
@EqualsAndHashCode(of = "id")
@Builder
public class LikeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private LikeId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id",referencedColumnName = "id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MessageEntity messageEntity;

//    @Column(name = "type",length = 10)
//    @Enumerated(EnumType.STRING)
//    private MessageOperateTypeEnum type;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"userId","messageId"})
    @Embeddable
    public static class LikeId implements Serializable{
        private static final long serialVersionUID = 1L;

        @Column(name = "user_id",length = 32)
        private String userId;

        @Column(name = "message_id")
        private Long messageId;
    }
}
