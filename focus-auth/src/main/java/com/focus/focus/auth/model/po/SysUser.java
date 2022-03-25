package com.focus.focus.auth.model.po;

import com.focus.focus.api.enumerate.SysUserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "sys_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 16 ,nullable = false,unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // 0-deactive 1-normal
    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private SysUserStatus status;

    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "update_at", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;

}
