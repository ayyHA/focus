package com.focus.focus.chat.domain.entity;

import com.focus.focus.api.enumerate.ChatStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "focus_chat")
@Builder
public class ChatEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ChatId id;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "text",length = 255)
    private String text;

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private ChatStatus status;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"sourceId","targetId"})
    @ToString(of={"sourceId","targetId"})
    private static class ChatId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "source_id", length = 32)
        private String sourceId;

        @Column(name = "target_id", length = 32)
        private String targetId;
    }
}
