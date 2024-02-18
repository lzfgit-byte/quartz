package com.power.job.queue;

import com.power.common.util.IdUtil;
import com.power.job.entity.PowerJob;
import com.power.job.job.JobExecute;
import com.power.job.manager.SchedulerManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DelayTask implements Delayed {

    private final Long taskId;

    private final long exeTime;
    private final PowerJob powerJob;
    private final Class<? extends JobExecute> jobExecute;
    private final SchedulerManager manager;

    public DelayTask(long exeTime, PowerJob job, Class<? extends JobExecute> jobExecute, SchedulerManager manager) {
        this.taskId = IdUtil.getId();
        this.exeTime = exeTime;
        this.powerJob = job;
        this.jobExecute = jobExecute;
        this.manager = manager;
    }

    public void doTask() {
        try {
            this.manager.executeOnce(this.powerJob, this.jobExecute);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public long getDelay(@NonNull TimeUnit unit) {
        return this.exeTime - System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NonNull Delayed o) {
        DelayTask t = (DelayTask) o;
        if (this.exeTime - t.exeTime < 0) {
            return -1;
        } else if (this.exeTime - t.exeTime == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "DelayTask{" +
                "taskId=" + taskId +
                ", exeTime=" + exeTime +
                '}';
    }
}
