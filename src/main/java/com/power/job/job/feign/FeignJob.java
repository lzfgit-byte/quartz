package com.power.job.job.feign;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class FeignJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        new FeignJobExecute(context).execute();
    }
}
