package com.focus.focus.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focus.auth.common.utils.OauthUtils;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.api.util.LoginVal;
import com.focus.focus.user.convertor.UserInfoConvertor;
import com.focus.focus.user.dao.UserRepository;
import com.focus.focus.user.domain.entity.UserEntity;
import com.focus.focus.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

//import com.focus.auth.common.model.LoginVal;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoConvertor userInfoConvertor;

    @Override
    public Boolean updateUserAvatar(String path) {
        LoginVal loginVal = OauthUtils.getCurrentUser();
        log.info("USERNAME: {}",loginVal.getUsername());
        UserEntity userEntity = userRepository.findByUsername(loginVal.getUsername());
        userEntity.setAvatarUrl(path);
        /* save动态更新 */
        UserEntity afterSaveEntity = userRepository.save(userEntity);
        if(Objects.isNull(afterSaveEntity))
            return false;
        return true;
    }

    @Override
    public Boolean updateUserBackground(String path) {
        LoginVal loginVal = OauthUtils.getCurrentUser();
        UserEntity userEntity = userRepository.findByUsername(loginVal.getUsername());
        userEntity.setProfileImageUrl(path);
        /* save动态更新 */
        UserEntity postSaveEntity = userRepository.save(userEntity);
        if(ObjectUtil.isEmpty(postSaveEntity))
            return false;
        return true;
    }

    @Override
    public UserInfoDto getUserInfoByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        log.info("getUserInfoByUsername entity birthday: {}",userEntity.getBirthday());
        if(Objects.isNull(userEntity))
            return null;
        UserInfoDto userInfoDto = userInfoConvertor.convertToDTO(userEntity);
        log.info("getUserInfoByUsername dto birthday: {}",userInfoDto.getBirthday());
        return userInfoDto;
    }

    @Override
    public UserInfoDto getUserInfoById(String userId) {
        Optional<UserEntity> opEntity = userRepository.findById(userId);
        UserEntity userEntity = null;
        if(opEntity.isPresent())
            userEntity = opEntity.get();
        else
            return null;
        UserInfoDto userInfoDto = userInfoConvertor.convertToDTO(userEntity);
        return userInfoDto;
    }

    @Override
    public UserInfoDto updateUserDetails(UserInfoDto dto){
        LoginVal loginVal = OauthUtils.getCurrentUser();
        UserEntity entity = userRepository.findByUsername(loginVal.getUsername());
        if(!StringUtils.isEmpty(dto.getNickname()))
            entity.setNickname(dto.getNickname());
        if(!StringUtils.isEmpty(dto.getDescription()))
            entity.setDescription(dto.getDescription());
        if(dto.getPinnedMessageId()!=null)
            entity.setPinnedMessageId(dto.getPinnedMessageId());
        if(dto.getGender()!=null)
            entity.setGender(dto.getGender());
        if(dto.getBirthday()!=null)
            entity.setBirthday(dto.getBirthday());
        // 只更新发生了改变的字段
        UserEntity afterSaveEntity = userRepository.save(entity);
//        log.info("entity's birthday: {}",afterSaveEntity.getBirthday()); CST时间
        Date dateOfBirthday = null;
//        if(afterSaveEntity.getBirthday()!=null){
//            DateTime dateTime = DateUtil.parseCST(afterSaveEntity.getBirthday().toString());
//            dateOfBirthday = DateUtil.parse(dateTime.toDateStr(), "yyyy-MM-dd");
//            log.info("dateOfBirthday: {}",dateOfBirthday);
//        }
        // 将新获得的userEntity转换为dto，重设时间
        UserInfoDto resDto = userInfoConvertor.convertToDTO(afterSaveEntity);
        log.info("updateUserInfo dto birthday: {}",resDto.getBirthday());
//        resDto.setBirthday(dateOfBirthday);
        return resDto;
    }

    @Override
    public List<UserInfoDto> getUserInfoDTOs(List<String> ids){
        if(CollectionUtil.isEmpty(ids))
            return null;    // ids为空
//        List<UserEntity> entities = userRepository.findAllById(ids);
        List<UserEntity> entities = new ArrayList<>();
        ids.forEach(id->{userRepository.findById(id).ifPresent(userEntity -> entities.add(userEntity));});
        log.info("entities: => [{}]",entities.toString());
        if(CollectionUtil.isEmpty(entities))
            return null;    // ids非空，但数据错误
        List<UserInfoDto> userInfoDtos = (List<UserInfoDto>)userInfoConvertor.convertToDTOList(entities);
        return userInfoDtos;
    }

    @Override
    public List<UserInfoDto> searchByNickname(String nickname) {
        // 判断字串非空
        if(StringUtils.isEmpty(nickname))
            return null;
        // 模糊匹配
        List<UserEntity> userEntities = userRepository.findByNicknameContaining(nickname);
        // 判断集合非空
        if(CollectionUtil.isEmpty(userEntities))
            return null;
        // 类型转换
        List<UserInfoDto> userInfoDtos = (List<UserInfoDto>) userInfoConvertor.convertToDTOList(userEntities);
        return userInfoDtos;
    }
}
