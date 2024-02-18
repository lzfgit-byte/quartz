package com.power.job.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.power.common.web.entity.PowerBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "job_params")
@Data
public class JobParams extends PowerBaseEntity {
    /**
     * 任务id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "job_id")
    private Long jobId;

    /**
     * 参数的key值
     */
    @TableField(value = "job_key")
    private String key;

    /**
     * 参数的value值
     */
    @TableField(value = "job_value")
    private String value;
}
