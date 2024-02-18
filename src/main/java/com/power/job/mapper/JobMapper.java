package com.power.job.mapper;

import com.power.common.web.mapper.PowerBaseMapper;
import com.power.job.entity.PowerJob;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lzf
 * @since 2023-05-26
 */
public interface JobMapper extends PowerBaseMapper<PowerJob> {

    PowerJob selectOne(@Param(value = "id") Long id);
}
