package com.ec.mapper;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface EmailverifycodeMapper {

    @Insert("INSERT INTO `email_verify_code`(email, code, type, expire_time) " +
            "VALUES(#{email}, #{code}, #{type}, #{expireTime})")
    int insert(@Param("email") String email,
               @Param("code") String code,
               @Param("type") String type,
               @Param("expireTime") LocalDateTime expireTime);

    /** 查询最新一条未使用、未过期的验证码 */
    @Select("SELECT code FROM `email_verify_code` " +
            "WHERE email=#{email} AND type=#{type} AND used=0 AND expire_time > NOW() " +
            "ORDER BY create_time DESC LIMIT 1")
    String selectLatestCode(@Param("email") String email, @Param("type") String type);

    @Update("UPDATE `email_verify_code` SET used=1 WHERE email=#{email} AND code=#{code} AND type=#{type}")
    int markUsed(@Param("email") String email,
                 @Param("code") String code,
                 @Param("type") String type);
}