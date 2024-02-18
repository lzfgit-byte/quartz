package com.power.job.listener;

import com.power.job.constant.JobConstant;
import com.power.job.entity.PowerJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;


@Slf4j
public class PowerSchedulerListener implements JobListener {
    public static final String LISTENER_NAME = "QuartSchedulerListener";


    @Override
    public String getName() {
        return LISTENER_NAME;
    }

    //任务被调度前
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        if (null != jobDataMap) {
            PowerJob job = (PowerJob) jobDataMap.get(JobConstant.JOB_DATA_KEY);
            assert job != null;
            log.info("任务 : " + job.getJobName() + "--" + job.getJobGroup() + " 将要执行");
        }
    }

    //任务调度被拒了
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        if (null != jobDataMap) {
            PowerJob job = (PowerJob) jobDataMap.get(JobConstant.JOB_DATA_KEY);
            assert job != null;
            log.info("任务：" + job.getJobName() + "--" + job.getJobGroup() + "执行调度被拒");
        }
    }

    //任务被调度后
    @Override
    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException) {
        JobDetail jobDetail = context.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        if (null != jobDataMap) {
            PowerJob job = (PowerJob) jobDataMap.get(JobConstant.JOB_DATA_KEY);
            assert job != null;
            if (jobException != null && !jobException.getMessage().equals("")) {
                log.error("任务 : " + job.getJobName() + "--" + job.getJobGroup() + " 执行结束" + jobException.getMessage(), jobException);
            }
            log.info("任务 : " + job.getJobName() + "--" + job.getJobGroup() + " 执行结束");
        }

    }
}
