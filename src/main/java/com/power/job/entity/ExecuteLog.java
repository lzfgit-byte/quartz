package com.power.job.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.power.common.util.IdUtil;
import com.power.common.web.entity.PowerBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "execute_log")
public class ExecuteLog extends PowerBaseEntity {

    public ExecuteLog() {
    }

    public ExecuteLog(PowerJob job, Date executeStartTime, Date executeEndTime, String executeParam, String executeRes, String executeResDetail) {
        this.setId(IdUtil.getId());
        this.setJobId(job.getId());
        this.setJobName(job.getJobName());
        this.setJobGroup(job.getJobGroup());
        this.setJobType(job.getJobType());
        this.setExecuteStartTime(executeStartTime);
        this.setExecuteParam(executeParam);
        this.setExecuteEndTime(executeEndTime);
        this.setExecuteRes(executeRes);
        this.setExecuteResDetail(executeResDetail);
    }

    /**
     * 任务id
     */
    @TableField(value = "job_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long jobId;
    /**
     * 任务名字
     */
    @TableField(value = "job_name")
    private String jobName;
    /**
     * 任务分组
     */
    @TableField(value = "job_group")
    private String jobGroup;
    /**
     * 任务类型
     */
    @TableField(value = "job_type")
    private String jobType;
    /**
     * 执行开始时间
     */
    @TableField(value = "execute_start_time")
    private Date executeStartTime;
    /**
     * 执行结果  (success)成功  (fail)失败
     */
    @TableField(value = "execute_res")
    private String executeRes;
    /**
     * 详细结果
     */
    @TableField(value = "execute_res_detail")
    private String executeResDetail;
    /**
     * 执行结束时间
     */
    @TableField(value = "execute_end_time")
    private Date executeEndTime;
    /**
     * 请求参数
     */
    @TableField(value = "execute_param")
    private String executeParam;

}
