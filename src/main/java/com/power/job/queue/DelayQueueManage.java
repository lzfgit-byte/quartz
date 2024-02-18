package com.power.job.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.DelayQueue;

@Slf4j
public class DelayQueueManage {
    public static final DelayQueue<DelayTask> queue = new DelayQueue<>();

    public static void addTask(DelayTask task) {
        queue.add(task);
    }

    public static void doDelayTask() {
        while (true) {
            try {
                DelayTask take = queue.take();
                System.out.println("queue do task....");
                take.doTask();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                break;
            }
        }
    }
}
