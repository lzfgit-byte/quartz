package com.power.job.utils;

import org.quartz.JobDataMap;

import java.util.*;

public class JobUtil {
    public static String[] STATIC_KEY = new String[]{};

    public static String getValueFromMap(JobDataMap jobDataMap, String key) {
        Object value = jobDataMap.get(key);
        assert value != null;
        return value.toString();
    }

    /**
     * 获取 key在pickKeys里的值
     *
     * @param jobDataMap 数据
     * @param pickKeys   需要提取的值
     * @return 提取后的值
     */
    public static Map<String, Object> pickMapKeys(JobDataMap jobDataMap, String... pickKeys) {
        Map<String, Object> resMap = new HashMap<>();
        Set<String> keySet = jobDataMap.keySet();
        List<String> kk = Arrays.asList(pickKeys);
        keySet.forEach(key -> {
            if (kk.contains(key)) {
                resMap.put(key, jobDataMap.get(key));
            }
        });
        return resMap;
    }

    /**
     * 获取 key在pickKeys里的值
     *
     * @param jobDataMap 数据
     * @param omitKeys   排除的值
     * @return 排除后的值
     */
    public static Map<String, Object> omitMapKeys(JobDataMap jobDataMap, String... omitKeys) {
        Map<String, Object> resMap = new HashMap<>();
        Set<String> keySet = jobDataMap.keySet();
        List<String> kk = Arrays.asList(omitKeys);
        keySet.forEach(key -> {
            if (!kk.contains(key)) {
                resMap.put(key, jobDataMap.get(key));
            }
        });
        return resMap;
    }
}
