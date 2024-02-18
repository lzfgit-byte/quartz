package com.power.job.manager;

import com.power.common.util.SpringContextUtil;
import com.power.job.constant.JobConstant;
import com.power.job.entity.PowerJob;
import com.power.job.job.JobExecute;
import com.power.job.queue.DelayQueueManage;
import com.power.job.queue.DelayTask;
import com.power.job.service.impl.ExecuteLogServiceImpl;
import com.power.job.service.impl.PowerJobService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class RetryPolicyManage {
    private final PowerJobService powerJobService = SpringContextUtil.getBean(PowerJobService.class);
    private final SchedulerManager schedulerManager = SpringContextUtil.getBean(SchedulerManager.class);

    private final ExecuteLogServiceImpl logService = SpringContextUtil.getBean(ExecuteLogServiceImpl.class);

    public void doRetryPolicy(PowerJob job, Class<? extends JobExecute> jobExecute) {
        Date s = new Date();
        if (JobConstant.RETRY_NOTHING.equals(job.getRetryPolicy())) {
            return;
        }
        if (JobConstant.RETRY_TERMINAL.equals(job.getRetryPolicy())) {
            boolean flag = powerJobService.terminalJob(job);
            logService.log(job, s, new Date(), "", flag ? "终止成功" : "终止失败", flag ? "重试策略中终止任务成功" : "重试策略中终止任务失败");
            return;
        }
        //延时
        if (JobConstant.RETRY_DELAY.equals(job.getRetryPolicy())) {
            logService.log(job, s, new Date(), "", "延时重试", "重试策略中延时重试任务");
            DelayQueueManage.addTask(new DelayTask(System.currentTimeMillis() + 1000 * 5, job, jobExecute, schedulerManager));
        }
    }
}
