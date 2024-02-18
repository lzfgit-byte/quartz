package com.power.job.service.impl;

import com.power.common.web.service.PowerBaseService;
import com.power.job.entity.PowerJob;
import com.power.job.mapper.JobMapper;
import com.power.job.service.IJobService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lzf
 * @since 2023-05-26
 */
@Service
public class JobServiceImpl extends PowerBaseService<JobMapper, PowerJob> implements IJobService {

    @Resource
    JobMapper jobMapper;

    @Override
    public PowerJob selectOne(Long id) {
        return jobMapper.selectOne(id);
    }
}
