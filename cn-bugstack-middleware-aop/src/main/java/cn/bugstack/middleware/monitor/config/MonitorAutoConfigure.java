package cn.bugstack.middleware.monitor.config;

import cn.bugstack.middleware.monitor.DoJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public DoJoinPoint point(){
        return new DoJoinPoint();
    }

}
