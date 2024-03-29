package com.focus.focus.pay.domain.entity;

import com.focus.focus.api.enumerate.PayStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "focus_pay")
@Data
@EqualsAndHashCode(of = "id")
@Builder
public class PayEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private PayId payId;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "amount_of_coin")
    private Long amountOfCoin;

    @Column(name = "status",length = 16)
    @Enumerated(EnumType.STRING)
    private PayStatus status;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = {"sourceId","targetId"})
    @ToString(of = {"sourceId","targetId"})
    public static class PayId implements Serializable{
        private static final long serialVersionUID = 1L;

        /**
         *  打赏的用户（给钱的）
         */
        @Column(name = "source_id", length = 32)
        private String sourceId;

        /**
         *  收取打赏的用户（收钱的）
         */
        @Column(name = "target_id", length = 32)
        private String targetId;
    }
}
