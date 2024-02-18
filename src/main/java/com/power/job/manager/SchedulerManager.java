package com.power.job.manager;

import cn.hutool.core.date.DateUtil;
import com.power.common.util.StringUtils;
import com.power.job.constant.JobConstant;
import com.power.job.entity.JobParams;
import com.power.job.entity.PowerJob;
import com.power.job.job.JobExecute;
import com.power.job.listener.PowerSchedulerListener;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class SchedulerManager {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    private JobListener scheduleListener;


    /**
     * 开始定时任务
     *
     * @param jobName  任务名字
     * @param jobGroup 任务分组
     * @throws SchedulerException 异常
     */
    public void startJob(String cron, String jobName, String jobGroup, Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (scheduleListener == null) {
            scheduleListener = new PowerSchedulerListener();
            scheduler.getListenerManager().addJobListener(scheduleListener);
        }
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if (!scheduler.checkExists(jobKey)) {
            scheduleJob(cron, scheduler, jobName, jobGroup, jobClass);
        }
    }

    /**
     * 开始定时任务
     *
     * @param powerJob 任务
     * @param jobClass 执行类
     * @throws SchedulerException 异常
     */
    public void startJob(PowerJob powerJob, Class<? extends Job> jobClass) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (scheduleListener == null) {
            scheduleListener = new PowerSchedulerListener();
            scheduler.getListenerManager().addJobListener(scheduleListener);
        }
        JobKey jobKey = new JobKey(powerJob.getJobName(), powerJob.getJobGroup());
        if (!scheduler.checkExists(jobKey)) {
            scheduleJob(powerJob, scheduler, jobClass);
        }
    }

    public void executeOnce(PowerJob powerJob, Class<? extends JobExecute> jobExecute) throws SchedulerException {
        JobDataMap jdm = new JobDataMap();
        List<JobParams> jobParams = powerJob.getJobParams();
        if (null != jobParams) {
            jobParams.forEach(jobParam -> {
                jdm.put(jobParam.getKey(), jobParam.getValue());
            });
            jdm.put(JobConstant.JOB_DATA_KEY, powerJob);
            try {
                Constructor<? extends JobExecute> constructor = jobExecute.getDeclaredConstructor(JobDataMap.class);
                constructor.setAccessible(true);
                JobExecute execute = constructor.newInstance(jdm);
                execute.execute(false);
            } catch (Exception e) {
                throw new SchedulerException();
            }
        }
    }

    /**
     * 移除定时任务
     *
     * @param jobName  任务名字
     * @param jobGroup 任务分类
     * @throws SchedulerException 异常
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

    }

    /**
     * 暂停定时任务
     *
     * @param jobName  任务名字
     * @param jobGroup 任务分类
     * @throws SchedulerException 异常
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
        }
    }

    /**
     * 恢复定时任务
     *
     * @param jobName  任务名字
     * @param jobGroup 任务分类
     * @throws SchedulerException 异常
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
        }
    }

    /**
     * 清空所有当前scheduler对象下的定时任务【目前只有全局一个scheduler对象】
     *
     * @throws SchedulerException 异常
     */
    public void clearAll() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.clear();
    }

    /**
     * @param cron 表单式
     * @return 下次五次的时间
     */
    public List<String> getNexCron(String cron) {
        if (StringUtils.isEmpty(cron)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        CronExpression cronExpression = null;
        try {
            cronExpression = new CronExpression(cron);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        String formatStr = "yyyy-MM-dd HH:mm:ss";
        Date one = cronExpression.getNextValidTimeAfter(new Date());
        Date two = cronExpression.getNextValidTimeAfter(one);
        Date three = cronExpression.getNextValidTimeAfter(two);
        Date four = cronExpression.getNextValidTimeAfter(three);
        Date five = cronExpression.getNextValidTimeAfter(four);
        res.add(DateUtil.format(one, formatStr));
        res.add(DateUtil.format(two, formatStr));
        res.add(DateUtil.format(three, formatStr));
        res.add(DateUtil.format(four, formatStr));
        res.add(DateUtil.format(five, formatStr));
        return res;

    }


    /**
     * 动态创建Job
     * 此处的任务可以配置可以放到properties或者是放到数据库中
     * Trigger:name和group 目前和job的name、group一致，之后可以扩展归类
     *
     * @param scheduler 调度器
     * @throws SchedulerException 异常
     */
    private void scheduleJob(String cron, Scheduler scheduler, String jobName, String jobGroup, Class<? extends Job> jobClass) throws SchedulerException {
        /*
         *  此处可以先通过任务名查询数据库，如果数据库中存在该任务，更新任务的配置以及触发器
         *  如果此时数据库中没有查询到该任务，则按照下面的步骤新建一个任务，并配置初始化的参数，并将配置存到数据库中
         */
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder).build();

        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    /**
     * 动态创建Job
     * 此处的任务可以配置可以放到properties或者是放到数据库中
     * Trigger:name和group 目前和job的name、group一致，之后可以扩展归类
     *
     * @param powerJob 任务
     * @throws SchedulerException 异常
     */
    private void scheduleJob(PowerJob powerJob, Scheduler scheduler, Class<? extends Job> jobClass) throws SchedulerException {
        JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(powerJob.getJobName(), powerJob.getJobGroup());
        JobDataMap jdm = new JobDataMap();
        List<JobParams> jobParams = powerJob.getJobParams();
        if (null != jobParams) {
            jobParams.forEach(jobParam -> {
                jdm.put(jobParam.getKey(), jobParam.getValue());
            });
            jdm.put(JobConstant.JOB_DATA_KEY, powerJob);
        }
        jobBuilder.setJobData(jdm);
        JobDetail jobDetail = jobBuilder.build();

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(powerJob.getCron());
        //构建触发器
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(powerJob.getJobName(), powerJob.getJobGroup()).withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }


}
