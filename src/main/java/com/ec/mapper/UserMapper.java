package com.ec.mapper;

import com.ec.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 * 整合了：
 *  - 用户模块（王梓骏）的增删改查
 *  - ec 模块（王柏程）的 findById / findAll / findByUsername
 */
public interface UserMapper {

    // ── 用户模块方法 ──────────────────────────────
    int    insert(User user);
    User   selectById(Long id);
    User   selectByUsername(String username);
    User   selectByEmail(String email);
    User   selectByWechatOpenid(String openid);
    List<User> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    int    countAll();
    int    updateInfo(User user);
    int    updatePassword(@Param("id") Long id, @Param("password") String password);
    int    updateStatus(@Param("id") Long id, @Param("status") String status);
    int    bindWechat(@Param("id") Long id, @Param("openid") String openid);
    int    updateVipLevel(@Param("id") Long id, @Param("isVip") Integer isVip);

    // ── ec 模块方法（原有，保持不变）──────────────
    User       findById(Long id);
    List<User> findAll();
    User       findByUsername(String username);
}