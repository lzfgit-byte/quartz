package com.power.job.event;

import com.power.job.queue.DelayQueueManage;
import com.power.job.service.impl.PowerJobService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartedEventListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        PowerJobService service = event.getApplicationContext().getBean(PowerJobService.class);
        service.onStartedJob();
        //启动延时队列
        new Thread(DelayQueueManage::doDelayTask).start();
    }
}
