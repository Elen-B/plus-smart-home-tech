package ru.practicum.mapper;

import ru.practicum.model.Sensor;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class Mapper {

    public static Sensor mapToSensor(HubEventAvro hubEventAvro, DeviceAddedEventAvro deviceAddedEventAvro) {
        return new Sensor(
                deviceAddedEventAvro.getId(),
                hubEventAvro.getHubId()
        );
    }
}
