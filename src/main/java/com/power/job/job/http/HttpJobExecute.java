package com.power.job.job.http;

import cn.hutool.http.HttpUtil;
import com.power.common.util.SpringContextUtil;
import com.power.job.constant.FeignInfoConstant;
import com.power.job.constant.HttpConstant;
import com.power.job.constant.JobConstant;
import com.power.job.entity.PowerJob;
import com.power.job.job.JobExecute;
import com.power.job.manager.RetryPolicyManage;
import com.power.job.service.IExecuteLogService;
import com.power.job.service.impl.ExecuteLogServiceImpl;
import com.power.job.utils.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.Date;
import java.util.Map;


@Slf4j
public class HttpJobExecute extends JobExecute {
    private final IExecuteLogService logService = SpringContextUtil.getBean(ExecuteLogServiceImpl.class);

    protected HttpJobExecute(JobExecutionContext context) {
        super(context);
    }

    protected HttpJobExecute(JobDataMap jobDataMap) {
        super(jobDataMap);
    }

    @Override
    public void execute() {
        this.execute(true);
    }

    @Override
    public void execute(boolean needRetry) {
        JobDataMap jobDataMap = null;
        if (this.context != null) {
            jobDataMap = this.context.getJobDetail().getJobDataMap();
        }
        if (this.jobDataMap != null) {
            jobDataMap = this.jobDataMap;
        }
        assert jobDataMap != null;
        String url = JobUtil.getValueFromMap(jobDataMap, FeignInfoConstant.URL);
        String method = JobUtil.getValueFromMap(jobDataMap, HttpConstant.METHOD);
        Map<String, Object> params = JobUtil.omitMapKeys(jobDataMap, FeignInfoConstant.FEIGN_NAME, FeignInfoConstant.URL, HttpConstant.METHOD, JobConstant.JOB_DATA_KEY);
        PowerJob job = (PowerJob) jobDataMap.get(JobConstant.JOB_DATA_KEY);
        Date start = new Date();
        String res = "";
        try {
            if (HttpConstant.GET.equals(method)) {
                res = HttpUtil.get(url, params);
            }
            if (HttpConstant.POST.equals(method)) {
                res = HttpUtil.post(url, params);
            }
            Date end = new Date();
            if (null != job) {
                if (res.length() > 3500) {
                    res = "返回超长：" + res.substring(0, 3500);
                }
                logService.log(job, start, end, params.toString(), JobConstant.JOB_EXECUTE_SUCCESS, res);
            }
        } catch (Exception e) {
            if (null != job) {
                log.error(job.getJobName() + "--" + job.getJobGroup() + "执行错误" + e.getMessage(), e);
                logService.log(job, start, new Date(), params.toString(), JobConstant.JOB_EXECUTE_FAIL, e.getMessage());
                if (needRetry) {
                    new RetryPolicyManage().doRetryPolicy(job, this.getClass());
                }
            }
        }
    }
}
