package com.power.job.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.power.common.web.entity.PowerBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "job")
public class PowerJob extends PowerBaseEntity {

    public PowerJob(String jobName, String jobGroup) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    public PowerJob() {
    }

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
     * 任务分类   fegin调用     http 调用
     */
    @TableField(value = "job_type")
    private String jobType;
    /**
     * quartz 表达式
     */
    @TableField(value = "cron")
    private String cron;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
    /**
     * 创建人id
     */
    @TableField(value = "create_user_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;
    /**
     * 创建人姓名
     */
    @TableField(value = "create_user_name")
    private String createUserName;
    /**
     * suspend（暂停） disabled(未运行)   enabled(运行中)   delete(删除)
     */
    @TableField(value = "job_status")
    private String status;
    /**
     * 重试策略 delay(延时重试)  terminal(终止任务)  nothing(不做处理) noButSend(不做处理但是通知)
     */
    @TableField(value = "retry_policy")
    private String retryPolicy;

    /**
     * 参数列表
     */
    @TableField(exist = false, updateStrategy = FieldStrategy.IGNORED)
    private List<JobParams> jobParams;
}
