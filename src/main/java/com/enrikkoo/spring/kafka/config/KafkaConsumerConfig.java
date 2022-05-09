package com.enrikkoo.spring.kafka.config;

import com.enrikkoo.spring.kafka.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, RequestDto> kafkaTemplate;

    @Value("${bootstrap.server}")
    private String bootstrapServers;

    @Value("${kafka.groupId.main}")
    private String kafkaConsumerGroupId;

    @Value("${sasl.jaas.config}")
    String SASL_JAAS_CONFIG;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.enrikkoo.spring.kafka.dto");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, SASL_JAAS_CONFIG);
        props.put("security.protocol", "SASL_SSL");
        return props;
    }

    @Bean
    public ConsumerFactory<String, RequestDto> consumerFactory() {
        ErrorHandlingDeserializer<RequestDto> errorHandlingDeserializer
                = new ErrorHandlingDeserializer<>(new JsonDeserializer<>(RequestDto.class));
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RequestDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        String topic = kafkaTopic + ".DLT";
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate,
                        (r, e) -> {
                            log.info("Send to " + topic);
                            return new TopicPartition(topic, r.partition());
                        }
                ),
                new FixedBackOff(1000L, 2L)));
        return factory;
    }
}
