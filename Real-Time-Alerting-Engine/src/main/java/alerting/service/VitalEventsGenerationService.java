package alerting.service;

import alerting.config.AppConfig;
import alerting.dto.VitalDTO;
import alerting.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class VitalEventsGenerationService {
    private final AppConfig appConfig;
    private final KafkaProducer<VitalDTO> vitalDTOKafkaProducer;

    @Scheduled(fixedRate = 1000)
    public void generateVitalEvents() {
        String time = String.valueOf(LocalDateTime.now());
        VitalDTO vitalDTO = new VitalDTO().builder()
                .time(time)
                .patientId("1001")
                .vitalName("HeartRate")
                .vitalValue(100.0).build();
        log.info("Pushing Vital Event in Kafka Queue");
        vitalDTOKafkaProducer.sendObjectToKafka(vitalDTO, appConfig.getVitalKafkaTopic());
    }
}
