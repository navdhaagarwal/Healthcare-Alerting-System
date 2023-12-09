package alerting.controller;

import alerting.config.AppConfig;
import alerting.dto.AlertDefinitionDTO;
import alerting.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/alerts")
public class AlertDefinitionController {

    private final AppConfig appConfig;
    private final KafkaProducer<AlertDefinitionDTO> alertDefinitionDTOKafkaProducer;

    @PostMapping("/add")
    public ResponseEntity<AlertDefinitionDTO> createAlertDefinition(@RequestBody AlertDefinitionDTO alertDefinition) {

        String alertId = UUID.randomUUID().toString();
        alertDefinition.setAlertID(alertId);
        alertDefinition.calculateKey();

        // Save the alert definition
        log.info("Pushing Alert definition in Kafka Queue:: {}", alertDefinition);
        alertDefinitionDTOKafkaProducer.sendObjectToKafka(alertDefinition, appConfig.getAlertDefinitionKafkaTopic());

        return ResponseEntity.ok(alertDefinition);
    }

}
