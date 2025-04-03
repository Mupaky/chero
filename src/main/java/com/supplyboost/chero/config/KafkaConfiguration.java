package com.supplyboost.chero.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {



    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name("notification-topic.v1").build();
    }
}
