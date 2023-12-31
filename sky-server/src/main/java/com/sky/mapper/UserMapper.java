package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getUserByOpenid(String openid);

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    void insert(User user);

    Integer countUserByDateTime(Map<String, LocalDateTime> dateTime);
}
