package com.power.job.job.http;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class HttpJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        new HttpJobExecute(context).execute();
    }
}
