package com.focus.focus.api.base;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseConvertor<ENTITY,DTO>{
    /**
     * 将ENTITY转换为DTO
     * @return
     */
    public abstract Function<ENTITY,DTO> functionConvertToDTO();

    /**
     *  将DTO转换为ENTITY
     * @return
     */
    public abstract Function<DTO,ENTITY> functionConvertToEntity();

    /**
     *  调用已经实现的Function接口函数将实体转换为DTO
     * @return
     */
    public Function<ENTITY,DTO> convertToDTO(){
        return functionConvertToDTO();
    }

    /**
     *  调用已经实现的Function接口函数将数据传输对象转换为ENTITY
     * @return
     */
    public Function<DTO,ENTITY> convertToEntity(){
        return functionConvertToEntity();
    }

    /**
     * 利用功能性接口实现的apply方法实现对象转换
     * @param entity
     * @return
     */
    public DTO convertToDTO(ENTITY entity){
        return convertToDTO().apply(entity);
    }

    /**
     * 利用功能性接口实现的apply方法实现对象转换
     * @param dto
     * @return
     */
    public ENTITY convertToEntity(DTO dto){
        return convertToEntity().apply(dto);
    }

    /* 以下为集合版的ENTITY和DTO互相转换 */
    public Function<Collection<ENTITY>,Collection<DTO>> functionConvertToDTOList(){
        return entities ->
            entities.stream().
            map(functionConvertToDTO()).collect(Collectors.toList());
    }

    public Function<Collection<DTO>,Collection<ENTITY>> functionConvertToEntityList(){
        return dtos ->
                dtos.stream().
                map(functionConvertToEntity()).collect(Collectors.toList());
    }

    public Function<Collection<ENTITY>,Collection<DTO>> convertToDTOList(){
        return functionConvertToDTOList();
    }

    public Function<Collection<DTO>,Collection<ENTITY>> convertToEntityList(){
        return functionConvertToEntityList();
    }

    public Collection<DTO> convertToDTOList(Collection<ENTITY> entities){
        return convertToDTOList().apply(entities);
    }

    public Collection<ENTITY> convertToEntityList(Collection<DTO> dtos){
        return convertToEntityList().apply(dtos);
    }
}
