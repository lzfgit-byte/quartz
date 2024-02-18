package com.power.job.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * 任务执行器
 */
public abstract class JobExecute {
    protected final JobExecutionContext context;
    protected final JobDataMap jobDataMap;

    protected JobExecute(JobExecutionContext context) {
        this.context = context;
        this.jobDataMap = null;
    }

    protected JobExecute(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
        this.context = null;
    }


    public abstract void execute();

    public abstract void execute(boolean needRetry);
}
