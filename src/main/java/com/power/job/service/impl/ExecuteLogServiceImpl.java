package com.power.job.service.impl;

import com.power.common.web.service.PowerBaseService;
import com.power.job.entity.ExecuteLog;
import com.power.job.entity.PowerJob;
import com.power.job.mapper.ExecuteLogMapper;
import com.power.job.service.IExecuteLogService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lzf
 * @since 2023-05-26
 */
@Service
public class ExecuteLogServiceImpl extends PowerBaseService<ExecuteLogMapper, ExecuteLog> implements IExecuteLogService {
    public void log(PowerJob job, Date executeStartTime, Date executeEndTime, String executeParam, String executeRes, String executeResDetail) {
        ExecuteLog executeLog = new ExecuteLog(job, executeStartTime, executeEndTime, executeParam, executeRes, executeResDetail);
        this.save(executeLog);
    }
}
