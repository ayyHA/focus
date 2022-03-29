package com.focus.focus.user.domain.entity;

import com.focus.focus.api.enumerate.GenderEnum;
import com.focus.focus.api.enumerate.LangSelect;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "focus_user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Data
@Builder
public class UserEntity implements Serializable{

    private static final long serialVersionUID = 1L;
    /**
     * 主键 UUID策略生成
     */
    @Id
    @GenericGenerator(name="userGenerator",strategy = "uuid")
    @GeneratedValue(generator = "userGenerator")
    @Column(length = 32)
    private String id;

    @Column(name = "username", length = 16, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 24, nullable = false)
    private String password;

    @Column(name = "nickname", length = 16, nullable = false, unique = true)
    private String nickname;

    @Column(name = "email", length = 30, nullable = false)
    private String email;

    /**
     * 用户注册时间
     */
    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            insertable = false,updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "description", length = 120)
    private String description;

    @Column(name = "birthday", columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    /**
     * 置顶讯息ID(message_id) 类型为LONG IDENTITY策略生成
     */
    @Column(name = "pinned_message_id")
    private Long pinnedMessageId;

    /**
     * 用户画像背景图
     */
    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "lang", length = 24)
    @Enumerated(EnumType.STRING)
    private LangSelect lang;

    /**
     * 用户头像
     */
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    /**
     * 虚拟货币：盾盾币
     */
    @Column(name = "dun_dun_coin")
    private Long dunDunCoin;

    // 后续增加两个虚拟列，以用作统计粉丝和关注的人
}

