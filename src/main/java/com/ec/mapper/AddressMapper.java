package com.ec.mapper;

import com.ec.pojo.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Insert("INSERT INTO `address`(user_id, receiver, phone, province, city, district, detail, postal_code, is_default) " +
            "VALUES(#{userId}, #{receiver}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{postalCode}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);

    @Select("SELECT * FROM `address` WHERE user_id = #{userId} ORDER BY is_default DESC")
    List<Address> selectByUserId(Long userId);

    @Select("SELECT * FROM `address` WHERE id = #{id}")
    Address selectById(Long id);

    @Update("UPDATE `address` SET receiver=#{receiver}, phone=#{phone}, province=#{province}, " +
            "city=#{city}, district=#{district}, detail=#{detail}, postal_code=#{postalCode} WHERE id=#{id}")
    int update(Address address);

    @Delete("DELETE FROM `address` WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);

    /** 将该用户所有地址取消默认，再设新默认 */
    @Update("UPDATE `address` SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefault(Long userId);

    @Update("UPDATE `address` SET is_default = 1 WHERE id = #{id}")
    int setDefault(Long id);
}