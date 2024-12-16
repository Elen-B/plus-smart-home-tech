package ru.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Scenario;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getType() {
        return ScenarioRemovedEventAvro.class.getName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(
                hubEventAvro.getHubId(),
                ((ScenarioRemovedEventAvro) hubEventAvro.getPayload()).getName());
        log.info("scenario for deleting {}", scenarioOpt);
        scenarioOpt.ifPresent(scenarioRepository::delete);
    }
}
