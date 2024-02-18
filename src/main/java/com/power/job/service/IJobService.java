package com.power.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.job.entity.PowerJob;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lzf
 * @since 2023-05-26
 */
public interface IJobService extends IService<PowerJob> {
    PowerJob selectOne(@Param(value = "id") Long id);
}
