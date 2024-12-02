package ru.practicum.config;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    public static final String SENSOR_EVENTS_TOPIC = "telemetry.sensors.v1";
    public static final String HUB_EVENTS_TOPIC = "telemetry.hubs.v1";

    @Bean
    public Producer<String, SpecificRecordBase> producer(
            @Value("${kafka.config.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.config.producer.key-serializer}") String keySerializer,
            @Value("${kafka.config.producer.value-serializer}") String valueSerializer
    ) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

        return new KafkaProducer<>(properties);
    }
}
