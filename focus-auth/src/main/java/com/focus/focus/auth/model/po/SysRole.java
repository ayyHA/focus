package com.focus.focus.auth.model.po;

import com.focus.focus.api.enumerate.SysRoleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_role")
@Builder
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", length = 16)
    private String roleName;

    @Column(name = "role_code", length = 16)
    private String roleCode;

    // 0-deactive 1-normal
    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private SysRoleStatus status;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "update_at", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
}
