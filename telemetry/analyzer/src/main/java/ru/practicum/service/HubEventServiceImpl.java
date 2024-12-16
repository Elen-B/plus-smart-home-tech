package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubEventServiceImpl implements HubEventService {
    private final Map<String, HubEventHandler> hubEventHandlers;

    public HubEventServiceImpl(Set<HubEventHandler> hubEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
    }

    @Override
    public void process(HubEventAvro hubEventAvro) {
        log.info("start process for {}", hubEventAvro);
        String type = hubEventAvro.getPayload().getClass().getName();
        if (hubEventHandlers.containsKey(type)) {
            log.info("process {}", hubEventHandlers.get(type));
            hubEventHandlers.get(type).handle(hubEventAvro);
        } else {
            throw new IllegalArgumentException("Не могу найти обработчик для события " + type);
        }
    }
}
