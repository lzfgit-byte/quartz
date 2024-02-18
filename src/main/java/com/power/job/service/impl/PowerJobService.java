package com.power.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.auth.entity.PowerSysUser;
import com.power.common.util.IdUtil;
import com.power.common.util.StringUtils;
import com.power.common.util.UserUtil;
import com.power.common.web.entity.PowerBaseEntity;
import com.power.job.constant.JobConstant;
import com.power.job.enmu.JobType;
import com.power.job.entity.ExecuteLog;
import com.power.job.entity.PowerJob;
import com.power.job.entity.JobParams;
import com.power.job.job.feign.FeignJob;
import com.power.job.job.feign.FeignJobExecute;
import com.power.job.job.http.HttpJob;
import com.power.job.job.http.HttpJobExecute;
import com.power.job.manager.SchedulerManager;
import com.power.job.service.IJobParamsService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PowerJobService {

    @Autowired
    JobServiceImpl jobService;
    @Autowired
    IJobParamsService jobParamsService;
    @Autowired
    SchedulerManager schedulerManager;
    @Autowired
    ExecuteLogServiceImpl logService;

    private void deleteJobParams(Long jobId) {
        assert null != jobId;
        LambdaQueryWrapper<JobParams> lqw = new LambdaQueryWrapper<>();
        lqw.eq(JobParams::getJobId, jobId);
        jobParamsService.remove(lqw);
    }

    public PowerJob selectOne(Long id) {

        return jobService.selectOne(id);
    }

    private void batchDeleteJobParams(List<Long> jobIds) {
        assert null != jobIds;
        LambdaQueryWrapper<JobParams> lqw = new LambdaQueryWrapper<>();
        lqw.in(JobParams::getJobId, jobIds);
        jobParamsService.remove(lqw);
    }

    private void saveJobParams(List<JobParams> jobParams) {
        if (null == jobParams || jobParams.size() == 0) {
            return;
        }
        jobParamsService.saveBatch(jobParams);
    }

    public String validateJob(String jobName, String jobGroup) {
        LambdaQueryWrapper<PowerJob> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PowerJob::getJobName, jobName);
        lqw.eq(PowerJob::getJobGroup, jobGroup);
        List<PowerJob> list = jobService.list(lqw);
        return list.size() == 0 ? "" : "已存在该任务，请变换任务名字或任务分组。";
    }

    public Object listJob(PowerJob job) {
        PowerSysUser info = UserUtil.info();
        if (info == null) {
            return new ArrayList<>();
        }
        job.setCreateUserId(info.getId());
        if (job.getPageNum() == null || job.getPageSize() == null) {
            return new ArrayList<>();
        } else {
            Page<PowerJob> page = new Page<>(job.getPageNum(), job.getPageSize());
            QueryWrapper<PowerJob> qw = (QueryWrapper<PowerJob>) jobService.getQueryWrapper(job);
            LambdaQueryWrapper<PowerJob> lambda = qw.lambda();
            lambda.eq(PowerJob::getCreateUserId, info.getId());
            lambda.orderByDesc(PowerJob::getCreateTime);
            return jobService.powerPage(page, qw);
        }
    }

    public boolean saveJob(PowerJob job) {
        assert null != job;
        Long parentId = IdUtil.getId();
        job.setId(parentId);
        job.setCreateTime(new Date());
        job.setUpdateTime(new Date());
        PowerSysUser userInfo = UserUtil.info();
        if (null != userInfo) {
            job.setCreateUserId(userInfo.getId());
            job.setCreateUserName(userInfo.getUsername());
        }
        job.getJobParams().forEach(item -> {
            item.setId(IdUtil.getId());
            item.setJobId(parentId);
        });
        jobService.save(job);
        saveJobParams(job.getJobParams());
        return true;
    }

    public boolean deleteJob(List<Long> ids) {
        assert null != ids;
        LambdaQueryWrapper<PowerJob> lqw = new LambdaQueryWrapper<>();
        lqw.in(PowerJob::getId, ids);
        jobService.remove(lqw);
        batchDeleteJobParams(ids);
        return true;
    }

    public boolean editJob(PowerJob job) {
        assert null != job;
        jobService.saveOrUpdate(job);
        deleteJobParams(job.getId());
        job.getJobParams().forEach(item -> {
            item.setId(IdUtil.getId());
            item.setJobId(job.getId());
        });
        saveJobParams(job.getJobParams());
        return true;
    }

    public boolean startJob(PowerJob job) {
        job = jobService.selectOne(job.getId());
        if (StringUtils.isEmpty(job.getCron())) {
            return false;
        }
        if (JobType.FEIGN.value.equals(job.getJobType())) {
            log.info("使用feign的执行器");
            try {
                schedulerManager.startJob(job, FeignJob.class);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                return false;
            }
            job.setStatus(JobConstant.ENABLED);
            jobService.saveOrUpdate(job);
            return true;
        }
        if (JobType.HTTP.value.equals(job.getJobType())) {
            log.info("使用http的执行器");
            try {
                schedulerManager.startJob(job, HttpJob.class);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                return false;
            }
            job.setStatus(JobConstant.ENABLED);
            jobService.saveOrUpdate(job);
            return true;
        }
        log.error("触发器调用失败");
        return false;
    }

    public void onStartedJob() {
        LambdaQueryWrapper<PowerJob> lqw = new LambdaQueryWrapper<>();
        lqw.eq(PowerJob::getStatus, JobConstant.ENABLED);
        lqw.ne(PowerJob::getCron, "");
        List<PowerJob> jobs = jobService.list(lqw);
        jobs.forEach(job -> {
            log.info("开始启动任务:" + job.getJobName() + "---" + job.getJobGroup());
            Date start = new Date();
            boolean isSuccess = startJob(job);
            log.info("启动成功" + job.getJobName() + "---" + job.getJobGroup());
            ExecuteLog executeLog = new ExecuteLog(job, start, new Date(), "", isSuccess ? "启动成功" : "启动失败", "");
            logService.save(executeLog);
        });
    }

    public boolean executeOnce(Long id) {
        PowerJob job = jobService.selectOne(id);
        if (JobType.FEIGN.value.equals(job.getJobType())) {
            log.info("使用feign的执行器");
            try {
                schedulerManager.executeOnce(job, FeignJobExecute.class);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                return false;
            }
            return true;
        }
        if (JobType.HTTP.value.equals(job.getJobType())) {
            log.info("使用http的执行器");
            try {
                schedulerManager.executeOnce(job, HttpJobExecute.class);
            } catch (SchedulerException e) {
                log.error(e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    //终止日志
    public boolean terminalJob(PowerJob job) {
        job = jobService.selectOne(job.getId());
        try {
            schedulerManager.deleteJob(job.getJobName(), job.getJobGroup());
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        job.setStatus(JobConstant.DISABLED);
        jobService.saveOrUpdate(job);
        return true;
    }

    public boolean pauseJob(PowerJob job) {
        job = jobService.selectOne(job.getId());
        try {
            schedulerManager.pauseJob(job.getJobName(), job.getJobGroup());
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        job.setStatus(JobConstant.SUSPEND);
        jobService.saveOrUpdate(job);
        return true;
    }

    public boolean resumeJob(PowerJob job) {
        job = jobService.selectOne(job.getId());
        try {
            schedulerManager.resumeJob(job.getJobName(), job.getJobGroup());
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        job.setStatus(JobConstant.ENABLED);
        jobService.saveOrUpdate(job);
        return true;
    }

    public boolean clearAllJob() {
        try {
            schedulerManager.clearAll();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public List<String> getNexCron(String cron) {
        return schedulerManager.getNexCron(cron);
    }

    public Object listExecuteLog(ExecuteLog log) {
        PowerSysUser info = UserUtil.info();
        if (info == null) {
            return new ArrayList<>();
        }
        List<Long> idLists = null;
        if (null == log.getJobId()) {
            LambdaQueryWrapper<PowerJob> lqw = new LambdaQueryWrapper<>();
            lqw.eq(PowerJob::getCreateUserId, info.getId());
            List<PowerJob> list = jobService.list(lqw);
            idLists = list.stream().map(PowerBaseEntity::getId).collect(Collectors.toList());
        } else {
            idLists = Collections.singletonList(log.getJobId());
        }
        if (idLists.size() == 0) {
            return new ArrayList<>();
        }
        if (log.getPageNum() == null || log.getPageSize() == null) {
            return new ArrayList<>();
        } else {
            Page<ExecuteLog> page = new Page<>(log.getPageNum(), log.getPageSize());
            QueryWrapper<ExecuteLog> qw = (QueryWrapper<ExecuteLog>) logService.getQueryWrapper(log);
            LambdaQueryWrapper<ExecuteLog> lambda = qw.lambda();
            lambda.in(ExecuteLog::getJobId, idLists);
            return logService.powerPage(page, lambda);
        }
    }


}
