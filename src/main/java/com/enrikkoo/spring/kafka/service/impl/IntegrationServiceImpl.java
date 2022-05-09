package com.enrikkoo.spring.kafka.service.impl;

import com.enrikkoo.spring.kafka.dto.RequestDto;
import com.enrikkoo.spring.kafka.exception.DeadLetterException;
import com.enrikkoo.spring.kafka.exception.RetryableException;
import com.enrikkoo.spring.kafka.service.IntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Override
    public void businessLogic(ConsumerRecord<String, RequestDto> record) {
        if ("retry".equalsIgnoreCase(record.value().getMessage())) {
            log.error("Retry offset " + record.offset());
            throw new RetryableException("RetryableException");
        }
        if ("error".equalsIgnoreCase(record.value().getMessage())) {
            log.error("Error offset " + record.offset());
            throw new DeadLetterException("DeadLetterException");
        }
        log.info("IntegrationService completed");
    }
}
