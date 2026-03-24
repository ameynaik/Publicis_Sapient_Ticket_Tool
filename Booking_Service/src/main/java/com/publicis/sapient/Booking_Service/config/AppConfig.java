package com.publicis.sapient.Booking_Service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.publicis.sapient.Booking_Service.kafka.KafkaTopics;

@Configuration
public class AppConfig {

    @Bean
    public NewTopic seatSyncTopic() {
        return new NewTopic(KafkaTopics.SEAT_SYNC_EVENTS, 3, (short) 1);
    }

    @Bean
    public NewTopic lockReleaseTopic() {
        return new NewTopic(KafkaTopics.LOCK_RELEASE_EVENTS, 3, (short) 1);
    }
}
