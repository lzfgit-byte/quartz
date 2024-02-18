package com.power.job.controller;

import com.power.common.result.Result;
import com.power.common.web.controller.PowerBaseController;
import com.power.job.entity.PowerJob;
import com.power.job.manager.SchedulerManager;
import com.power.job.service.impl.JobServiceImpl;
import com.power.job.service.impl.PowerJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/powerJob")
public class PowerJobController extends PowerBaseController<JobServiceImpl, PowerJob> {

    @Autowired
    public SchedulerManager scheduler;
    @Autowired
    public PowerJobService powerJobService;

    /**
     * 获取所有的任务
     */
    @RequestMapping(value = "/listJob", method = RequestMethod.POST)
    public Result<?> listJob(@RequestBody PowerJob job) {

        return Result.success(powerJobService.listJob(job));
    }

    /**
     * 获取所有的任务
     */
    @RequestMapping(value = "/selectOne", method = RequestMethod.GET)
    public Result<PowerJob> selectOne(@RequestParam Long id) {
        return Result.success(powerJobService.selectOne(id));
    }

    /**
     * 保存任务
     */
    @RequestMapping(value = "/saveJob")
    public Result<?> saveJob(@RequestBody PowerJob job) {
        return powerJobService.saveJob(job) ? Result.success("保存成功") : Result.failure("保存失败");
    }

    /**
     * 保存任务
     */
    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    public Result<?> validateJob(@RequestParam String jobName, @RequestParam String jobGroup) {
        return Result.success(powerJobService.validateJob(jobName, jobGroup));
    }

    /**
     * 编辑保存任务
     */
    @RequestMapping(value = "/editJob")
    public Result<?> editJob(@RequestBody PowerJob job) {
        return powerJobService.editJob(job) ? Result.success("编辑成功") : Result.failure("编辑失败");
    }

    /**
     * 删除
     */
    @PostMapping(value = "/deleteJobs")
    public Result<?> deleteJobs(@RequestBody List<Long> ids) {
        return powerJobService.deleteJob(ids) ? Result.success("删除成功") : Result.failure("删除失败");
    }

    /**
     * 启动任务
     */
    @RequestMapping(value = "/startJob")
    public Result<?> startJob(@RequestBody PowerJob job) {
        return powerJobService.startJob(job) ? Result.success("启动任务成功") : Result.failure("启动任务失败");

    }

    /**
     * 执行一次
     *
     * @return 返回
     */
    @RequestMapping(value = "/executeOnce", method = RequestMethod.GET)
    public Result<?> executeOnce(@RequestParam Long id) {
        return powerJobService.executeOnce(id) ? Result.success("执行一次成功") : Result.failure("执行一次失败");
    }

    /**
     * 终止任务
     */
    @RequestMapping(value = "/terminalJob")
    public Result<?> terminalJob(@RequestBody PowerJob job) {
        return powerJobService.terminalJob(job) ? Result.success("终止成功") : Result.failure("终止失败");
    }

    /**
     * 暂停任务
     */
    @RequestMapping(value = "/pauseJob")
    public Result<?> pauseJob(@RequestBody PowerJob job) {
        return powerJobService.pauseJob(job) ? Result.success("暂停成功") : Result.failure("暂停失败");
    }

    /**
     * 恢复任务
     */
    @RequestMapping(value = "/resumeJob")
    public Result<?> resumeJob(@RequestBody PowerJob job) {
        return powerJobService.resumeJob(job) ? Result.success("恢复成功") : Result.failure("恢复失败");
    }

    /**
     * 删除所有任务
     */
    @RequestMapping(value = "/clearAllJob")
    public Result<?> clearAllJob() {
        return powerJobService.clearAllJob() ? Result.success("清除所有任务成功") : Result.failure("清除所有任务失败");
    }

    /**
     * 删除所有任务
     */
    @RequestMapping(value = "/getNexCron", method = RequestMethod.GET)
    public Result<List<String>> getNexCron(@RequestParam String cron) {
        return Result.success(powerJobService.getNexCron(cron));
    }

}
