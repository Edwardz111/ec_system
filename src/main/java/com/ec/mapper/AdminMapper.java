package com.ec.mapper;

import com.ec.pojo.Admin;
import com.ec.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    /** 根据 users 表主键查询是否在 admin 表中有管理员记录 */
    @Select("SELECT id, user_id AS userId, permission_level AS permissionLevel, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM admin WHERE user_id = #{userId} LIMIT 1")
    Admin findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO admin (user_id, permission_level, created_at, updated_at) " +
            "VALUES (#{userId}, #{permissionLevel}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Update("UPDATE admin SET permission_level = #{permissionLevel}, updated_at = NOW() WHERE user_id = #{userId}")
    int updatePermissionLevel(@Param("userId") Long userId, @Param("permissionLevel") String permissionLevel);

    @Delete("DELETE FROM admin WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM users")
    List<User> findAllUsers();

    @Select("<script>" +
            "SELECT * FROM users WHERE 1=1" +
            "<if test=\"username != null and username != ''\"> AND username LIKE CONCAT('%',#{username},'%')</if>" +
            "<if test=\"status != null and status != ''\"> AND status = #{status}</if>" +
            "<if test=\"vip != null\"> AND is_vip = #{vip}</if>" +
            "</script>")
    List<User> searchUsers(
            @Param("username") String username,
            @Param("status") String status,
            @Param("vip") Integer vip
    );
}
