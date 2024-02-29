package ke.co.pesalink.papss.incoming.credittransferproducerservice.configs;

import ke.co.pesalink.papss.incoming.credittransferproducerservice.service.PollingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.time.Duration;

//@Component
@RequiredArgsConstructor
public class SchedulingConfig implements SchedulingConfigurer {

    private final PollingService pollingService;
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(pollingService, Duration.ofSeconds(10));
    }
}
