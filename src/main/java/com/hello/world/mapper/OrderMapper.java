package com.hello.world.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by Administrator on 2016/5/23.
 */
@Mapper
public interface OrderMapper {
    @Select("select orderid from testorder where orderid like #{name} order by id desc limit 3")
    List<String> find(@Param("name") String name);
}
