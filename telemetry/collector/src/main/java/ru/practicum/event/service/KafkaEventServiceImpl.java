package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.hub.HubEvent;
import ru.practicum.event.model.sensor.SensorEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventServiceImpl implements EventService {
    @Override
    public void collectSensorEvent(SensorEvent sensorEvent) {

    }

    @Override
    public void collectHubEvent(HubEvent hubEvent) {

    }
}
