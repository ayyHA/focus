package com.focus.focus.message.domain.entity;

import com.focus.focus.api.enumerate.LikeStatus;
import com.focus.focus.api.enumerate.MessageOperateStatus;
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
@ToString(exclude = "messageEntity")
@Builder
public class LikeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private LikeId id;

//    若是通过此种方式维护多对一的关系，会增加与数据库的交互，
//    自行维护（前端传messageId）,而非save时通过entity的主键映射到messageId上去
//    （按我的写法会加大对数据库的访问，故废弃）
//    @MapsId("messageId")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "message_id",referencedColumnName = "id",
//            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private MessageEntity messageEntity;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    // 是否已发送通知
    @Column(name="status",length = 16)
    @Enumerated(EnumType.STRING)
    private MessageOperateStatus status;

    // 是否喜欢
    @Column(name="likeStatus",length = 16)
    @Enumerated(EnumType.STRING)
    private LikeStatus likeStatus;

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
