package ru.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Action;
import ru.practicum.model.Condition;
import ru.practicum.model.Scenario;
import ru.practicum.repository.ActionRepository;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;

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
        Scenario scenario;
        String logAction;
        if (scenarioOpt.isEmpty()) {
            scenario = Mapper.mapToScenario(hubEventAvro, scenarioAddedEventAvro);
            logAction = "added";
        } else {
            scenario = scenarioOpt.get();
            List<Condition> conditions = new ArrayList<>(scenarioAddedEventAvro.getConditions().stream()
                    .map(conditionAvro -> Mapper.mapToCondition(scenario, conditionAvro))
                    .toList());
            List<Action> actions = new ArrayList<>(scenarioAddedEventAvro.getActions().stream()
                    .map(actionAvro -> Mapper.mapToAction(scenario, actionAvro))
                    .toList());
            scenario.setConditions(conditions);
            scenario.setActions(actions);
            logAction = "updated";
        }
        scenarioRepository.save(scenario);
        log.info("{} scenario {}", logAction, scenario);
    }
}
