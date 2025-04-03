package com.supplyboost.chero.event;


import com.supplyboost.chero.event.payload.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRegisteredEventProducer {
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Autowired
    public UserRegisteredEventProducer(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(UserRegisteredEvent event) {
        kafkaTemplate.send("notification-topic.v1", event);
        log.info("Sent UserRegisteredEvent to Kafka topic: {}", event);
    }
}
