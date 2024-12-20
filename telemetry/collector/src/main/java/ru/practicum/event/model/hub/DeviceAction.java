package ru.practicum.event.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAction {

    @NotBlank
    String sensorId;

    @NotNull
    ActionType type;

    Integer value;
}
