package com.focus.focus.user.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 本表意在维护用户间的关注信息，当用户需要时，可以通过虚拟列进行获得（如粉丝数、关注的人数量）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "Id")
@Table(name = "focus_friendship")
@Entity
@Builder
public class FriendshipEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 复合主键：sourceId + targetId
     */
    @EmbeddedId
    private FriendshipId id;

    @MapsId("sourceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id",referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity sourceUser;

    @MapsId("targetId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id",referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserEntity targetUser;

    @Column(name = "follow_date",columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date followDate;

    /**
     * 可嵌入类
     */
    @Embeddable
    @EqualsAndHashCode(of = {"sourceId","targetId"})
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FriendshipId implements Serializable{
        private static final long serialVersionUID = 1L;

        @Column(name = "source_id",length = 32)
        private String sourceId;

        @Column(name = "target_id",length = 32)
        private String targetId;
    }
}
