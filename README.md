# Spring Kafka Consumer
## Important properties for Confluent Cloud
spring.kafka.properties.bootstrap.servers=URL\
spring.kafka.properties.sasl.mechanism=PLAIN\
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule   required username='<API_KEY>'   password='<API_SECRET>';\
spring.kafka.properties.security.protocol=SASL_SSL
