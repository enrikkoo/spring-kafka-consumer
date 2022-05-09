package com.enrikkoo.spring.kafka.service;

import com.enrikkoo.spring.kafka.dto.RequestDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IntegrationService {

    void businessLogic(ConsumerRecord<String, RequestDto> record);
}
