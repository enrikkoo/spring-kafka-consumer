# Spring Kafka Consumer
![schema](https://user-images.githubusercontent.com/48565679/167512270-9df4394e-901f-48ab-9cf1-83caa805c5a9.png)

## Important properties for Confluent Cloud
spring.kafka.properties.bootstrap.servers=URL\
spring.kafka.properties.sasl.mechanism=PLAIN\
spring.kafka.properties.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule   required username='<API_KEY>'   password='<API_SECRET>';\
spring.kafka.properties.security.protocol=SASL_SSL
