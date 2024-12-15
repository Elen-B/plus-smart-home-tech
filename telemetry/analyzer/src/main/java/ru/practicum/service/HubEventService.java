package ru.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {
    void process(HubEventAvro hubEventAvro);
}
