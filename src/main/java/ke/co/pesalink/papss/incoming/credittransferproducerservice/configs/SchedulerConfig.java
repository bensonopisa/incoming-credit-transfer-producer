package ke.co.pesalink.papss.incoming.credittransferproducerservice.configs;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.service.PollerService;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SchedulerConfig implements SchedulingConfigurer {
    private final PollerService pollerService;

    public SchedulerConfig(PollerService pollerService) {
        this.pollerService = pollerService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(pollerService, Duration.ofSeconds(5));
    }
}
