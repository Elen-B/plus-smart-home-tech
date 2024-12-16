package ru.practicum.mapper;

import ru.practicum.model.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

public class Mapper {

    public static Sensor mapToSensor(HubEventAvro hubEventAvro, DeviceAddedEventAvro deviceAddedEventAvro) {
        return new Sensor(
                deviceAddedEventAvro.getId(),
                hubEventAvro.getHubId()
        );
    }

    public static Scenario mapToScenario(HubEventAvro hubEventAvro, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        return new Scenario(
                null,
                hubEventAvro.getHubId(),
                scenarioAddedEventAvro.getName(),
                scenarioAddedEventAvro.getConditions().stream()
                        .map(conditionAvro -> mapToCondition(hubEventAvro, conditionAvro))
                        .toList(),
                scenarioAddedEventAvro.getActions().stream()
                        .map(actionAvro -> mapToAction(hubEventAvro, actionAvro))
                        .toList()
        );
    }

    public static Condition mapToCondition(HubEventAvro hubEventAvro, ScenarioConditionAvro conditionAvro) {
        return Condition.builder()
                .sensor(new Sensor(conditionAvro.getSensorId(), hubEventAvro.getHubId()))
                .type(toConditionType(conditionAvro.getType()))
                .operation(toConditionOperation(conditionAvro.getOperation()))
                .value(getConditionValue(conditionAvro.getValue()))
                .build();
    }

    public static Action mapToAction(HubEventAvro hubEventAvro, DeviceActionAvro deviceActionAvro) {
        Action action = Action.builder()
                .sensor(new Sensor(deviceActionAvro.getSensorId(), hubEventAvro.getHubId()))
                .type(toActionType(deviceActionAvro.getType()))
                .value(deviceActionAvro.getValue())
                .build();
        System.out.println("action = " + action);
        return action;
    }

    public static ConditionType toConditionType(ConditionTypeAvro conditionTypeAvro) {
        return ConditionType.valueOf(conditionTypeAvro.name());
    }

    public static ConditionOperation toConditionOperation(ConditionOperationAvro conditionOperationAvro) {
        return ConditionOperation.valueOf(conditionOperationAvro.name());
    }

    public static ActionType toActionType(ActionTypeAvro actionTypeAvro) {
        return ActionType.valueOf(actionTypeAvro.name());
    }

    public static Integer getConditionValue(Object conditionValue) {
        if (conditionValue == null) {
            return null;
        }
        if (conditionValue instanceof Boolean) {
            return ((Boolean) conditionValue ? 1 : 0);
        }
        if (conditionValue instanceof Integer) {
            return (Integer) conditionValue;
        }
        throw new ClassCastException("Ошибка преобразования значения");
    }
}
