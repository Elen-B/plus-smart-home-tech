package ru.practicum.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.model.sensor.SensorEvent;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("POST /events/sensors with body {} ", event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody SensorEvent event) {
        log.info("POST /events/hubs with body {} ", event);
    }
}
