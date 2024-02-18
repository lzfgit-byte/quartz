package com.power.job.controller;

import com.power.common.result.Result;
import com.power.common.web.controller.PowerBaseController;
import com.power.job.entity.ExecuteLog;
import com.power.job.service.impl.ExecuteLogServiceImpl;
import com.power.job.service.impl.PowerJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/powerJobExecuteLog")
public class ExecuteLogController extends PowerBaseController<ExecuteLogServiceImpl, ExecuteLog> {

    @Autowired
    PowerJobService powerJobService;

    /**
     * 获取所有的任务
     */
    @RequestMapping(value = "/listExecuteLog", method = RequestMethod.POST)
    public Result<?> listExecuteLog(@RequestBody ExecuteLog log) {
        return Result.success(powerJobService.listExecuteLog(log));
    }
}
