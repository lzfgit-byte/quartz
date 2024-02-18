package com.power.job.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * suspend: '暂停',
 * disabled: '停止',
 * enabled: '运行中',
 */
public class JobConstant {
    public static final String SUSPEND = "suspend";
    public static final String DISABLED = "disabled";
    public static final String ENABLED = "enabled";

    public static final Map<String, String> dictMap = new HashMap<>();

    static {
        dictMap.put(SUSPEND, "暂停");
        dictMap.put(DISABLED, "未运行");
        dictMap.put(ENABLED, "运行中");
    }

    //
    public static final String JOB_DATA_KEY = "powerJob";
    //执行结果
    public static final String JOB_EXECUTE_SUCCESS = "success";
    public static final String JOB_EXECUTE_FAIL = "fail";
    //重试策略
    public static final String RETRY_DELAY = "delay";
    public static final String RETRY_TERMINAL = "terminal";
    public static final String RETRY_NOTHING = "nothing";

}
