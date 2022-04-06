package com.focus.focus.user.convertor;

import com.focus.focus.api.base.BaseConvertor;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserInfoConvertor extends BaseConvertor<UserEntity, UserInfoDto>{
    @Override
    public Function<UserEntity, UserInfoDto> functionConvertToDTO() {
        return userEntity -> {
            return UserInfoDto.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .nickname(userEntity.getNickname())
                    .email(userEntity.getEmail())
                    .createAt(userEntity.getCreateAt())
                    .description(userEntity.getDescription())
                    .birthday(userEntity.getBirthday())
                    .gender(userEntity.getGender())
                    .pinnedMessageId(userEntity.getPinnedMessageId())
                    .profileImageUrl(userEntity.getProfileImageUrl())
                    .avatarUrl(userEntity.getAvatarUrl())
                    .dunDunCoin(userEntity.getDunDunCoin())
                    .build();
        };
    }

    @Override
    public Function<UserInfoDto, UserEntity> functionConvertToEntity() {
        return userInfoDto -> {
            return UserEntity.builder()
                    .nickname(userInfoDto.getNickname())
                    .description(userInfoDto.getDescription())
                    .birthday(userInfoDto.getBirthday())
                    .gender(userInfoDto.getGender())
                    .pinnedMessageId(userInfoDto.getPinnedMessageId())
                    .build();
        };
    }
}
