package ru.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Scenario;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getType() {
        return ScenarioAddedEventAvro.class.getName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hubEventAvro) {
        ScenarioAddedEventAvro scenarioAddedEventAvro = (ScenarioAddedEventAvro) hubEventAvro.getPayload();
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(
                hubEventAvro.getHubId(),
                scenarioAddedEventAvro.getName());
        if (scenarioOpt.isEmpty()) {
            Scenario scenario = Mapper.mapToScenario(hubEventAvro, scenarioAddedEventAvro);
            scenarioRepository.save(scenario);
            log.info("added scenario {}", scenario);
        }
    }
}
