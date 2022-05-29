package com.focus.focus.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focus.auth.common.utils.OauthUtils;
import com.focus.focus.api.dto.SignDto;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.api.util.LoginVal;
import com.focus.focus.user.convertor.UserInfoConvertor;
import com.focus.focus.user.dao.UserRepository;
import com.focus.focus.user.domain.entity.UserEntity;
import com.focus.focus.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

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

    // 用户签到 [key]sign:userId:yyyyMM [offset]dayOffset [value]true/false
    @Override
    public SignDto signFocus(String userId, Date date) {
        // 获取当前的天数作为offset，从0开始
        long dayOffset = DateUtil.dayOfMonth(date) - 1;
        // 获得signKey
        String signKey = getSignKey(userId,date);
        // 查看今天是否已经签到
        Boolean isSign = redisTemplate.opsForValue().getBit(signKey,dayOffset);
        // 已签到，返回null
        if(Objects.equals(isSign,Boolean.TRUE))
            return null;
        // 未签到则签个到
        redisTemplate.opsForValue().setBit(signKey,dayOffset,true);
        // 获取当月连续签到天数
        Integer signContinuous = getSignContinuous(userId,date);
        // 获取当月签到总天数
        Integer signCount = getSignCount(userId, date);
        // 增加的盾盾币数量
        Long signDunDunCoin = updateDunDunCoin(userId,signContinuous);
        // 返回SignDto对象
        return SignDto.builder().signContinuous(signContinuous)
                .signCount(signCount).signStatus(true).signDunDunCoin(signDunDunCoin).build();
    }

    @Override
    public Boolean getSignStatus(String userId, Date date) {
        String signKey = getSignKey(userId,date);
        int dayOffset = DateUtil.dayOfMonth(date) -1 ;
        Boolean isSign = redisTemplate.opsForValue().getBit(signKey, dayOffset);
        log.info("dayOffset: [{}]",dayOffset);
        if(Objects.equals(isSign,Boolean.TRUE))
            return true;
        return false;
    }

    // 获取当月连续签到次数
    private Integer getSignContinuous(String userId, Date date) {
        int dayOffset = DateUtil.dayOfMonth(date);
        String signKey = getSignKey(userId,date);
        // bitfield signKey get uDayOffset 0 即获取到今日为止的本月的二进制位
        BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create();
        bitFieldSubCommands.get(BitFieldSubCommands.BitFieldType.unsigned(dayOffset)).valueAt(0);
        // 调用redisTemplate的bitfield方法
        List<Long> list = redisTemplate.opsForValue().bitField(signKey, bitFieldSubCommands);
        if(list == null || list.isEmpty())
            return 0;
        // 连续签到天数
        int signContinuous = 0;
        long bits = list.get(0) == null ? 0 : list.get(0);
        // 通过位运算，对每一位（即每一天）的签到情况进行检验，当连续签到中断则返回连续签到天数
        for(int i = dayOffset;i>0;i--){
            // 右移再左移，校验最末位是否已签到，未签到则会相等
            if(bits>>1<<1 == bits) {
                // 排除今日，今日可能未签到;若不是今日，则表示连签已断
                if (i != dayOffset)
                    break;
            }
            else{
                signContinuous++;
            }
            bits>>=1;
        }
        return signContinuous;
    }

    // 获取当月签到总次数
    private Integer getSignCount(String userId, Date date) {
        String signKey = getSignKey(userId,date);
        Long signCountL = redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(signKey.getBytes()));
        return signCountL==null?0:signCountL.intValue();
    }

    // 获取签到的key [key]sign:userId:yyyyMM [offset]dayOffset [value]true/false
    private String getSignKey(String userId,Date date){
        return String.format("sign:%s:%s",userId, DateUtil.format(date,"yyyyMM"));
    }

    // 更新盾盾币数量
    private Long updateDunDunCoin(String userId,Integer signContinuous){
        long incrCoin = 0;
        long[] level = {100,150,200,250,300,400};
        if(signContinuous>4)
            incrCoin = level[5];
        else
            incrCoin = level[signContinuous];
        Optional<UserEntity> opId = userRepository.findById(userId);
        if(opId.isPresent()){
            UserEntity userEntity = opId.get();
            userEntity.setDunDunCoin(userEntity.getDunDunCoin() + incrCoin);
            userRepository.save(userEntity);
        }
        return incrCoin;
    }
}
