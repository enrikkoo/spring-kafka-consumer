package com.enrikkoo.spring.kafka.consumer;

import com.enrikkoo.spring.kafka.dto.RequestDto;
import com.enrikkoo.spring.kafka.exception.DeadLetterException;
import com.enrikkoo.spring.kafka.exception.RetryableException;
import com.enrikkoo.spring.kafka.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final IntegrationService integrationService;
    private final KafkaTemplate<String, RequestDto> kafkaTemplate;

    @KafkaListener(topics = "#{'${kafka.topic}'}", groupId = "#{'${kafka.groupId.main}'}")
    public void consume(final ConsumerRecord<String, RequestDto> consumerRecord) {
        log.info("Start receiving main " + consumerRecord.offset());

        try {
            integrationService.businessLogic(consumerRecord);
        } catch (DeadLetterException e) {
            log.info("Send to DLT ");
            kafkaTemplate.send(consumerRecord.topic() + ".DLT", consumerRecord.value());
        } catch (RetryableException e) {
            log.info("Send to Retry ");
            kafkaTemplate.send(consumerRecord.topic() + ".Retry", consumerRecord.value());
        } catch (Exception e) {
            log.info("Unexpected exception, send to retry ");
            kafkaTemplate.send(consumerRecord.topic() + ".Retry", consumerRecord.value());
        }
        log.info("Message processed main  " + consumerRecord.offset());
    }

    @KafkaListener(topics = "#{'${kafka.topic.retry}'}", groupId = "#{'${kafka.groupId.retry}'}")
    public void consumeRetry(final ConsumerRecord<String, RequestDto> consumerRecord) {
        log.info("Start receiving retry " + consumerRecord.offset());
        integrationService.businessLogic(consumerRecord);
        log.info("Message processed retry " + consumerRecord.offset());
    }
}
