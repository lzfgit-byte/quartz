package com.power.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.job.entity.ExecuteLog;
import com.power.job.entity.PowerJob;

import java.util.Date;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lzf
 * @since 2023-05-26
 */
public interface IExecuteLogService extends IService<ExecuteLog> {
    void log(PowerJob job, Date executeStartTime, Date executeEndTime, String executeParam, String executeRes, String executeResDetail);
}
