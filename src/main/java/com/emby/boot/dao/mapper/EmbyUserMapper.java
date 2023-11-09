package com.emby.boot.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emby.boot.dao.entity.EmbyUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author laojian
 * @date 2023/10/9 14:15
 */
@Mapper
public interface EmbyUserMapper extends BaseMapper<EmbyUser> {
}
