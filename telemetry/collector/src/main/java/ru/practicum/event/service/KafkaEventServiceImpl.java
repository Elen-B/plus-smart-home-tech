package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.config.KafkaConfig;
import ru.practicum.event.mapper.HubEventMapper;
import ru.practicum.event.mapper.SensorEventMapper;
import ru.practicum.event.model.hub.HubEvent;
import ru.practicum.event.model.sensor.SensorEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventServiceImpl implements EventService {
    private final Producer<String, SpecificRecordBase> producer;

    @Override
    public void collectSensorEvent(SensorEvent sensorEvent) {
        send(KafkaConfig.SENSOR_EVENTS_TOPIC,
                sensorEvent.getHubId(),
                sensorEvent.getTimestamp().toEpochMilli(),
                SensorEventMapper.toSensorEventAvro(sensorEvent));
    }

    @Override
    public void collectHubEvent(HubEvent hubEvent) {
        send(KafkaConfig.HUB_EVENTS_TOPIC,
                hubEvent.getHubId(),
                hubEvent.getTimestamp().toEpochMilli(),
                HubEventMapper.toHubEventAvro(hubEvent));
    }

    private void send(String topic, String key, Long timestamp, SpecificRecordBase specificRecordBase) {
        ProducerRecord<String, SpecificRecordBase> rec = new ProducerRecord<>(
                topic,
                null,
                timestamp,
                key,
                specificRecordBase);
        producer.send(rec);
    }
}
