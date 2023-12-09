package alerting.service;

import alerting.config.AppConfig;
import alerting.dto.AlertDefinitionDTO;
import alerting.dto.VitalDTO;
import alerting.kafka.KafkaProducer;
import alerting.util.TimeConversion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class VitalEventsGenerationService {
    private final AppConfig appConfig;
    private final KafkaProducer<VitalDTO> vitalDTOKafkaProducer;
    private final KafkaProducer<AlertDefinitionDTO> alertDefinitionDTOKafkaProducer;
    private Map<String, List<VitalDTO>> dataHR;
    private int index;
    private List<String> patientIds;

    @PostConstruct
    public void init() {
        patientIds = List.of(new String[]{"138022"});
        dataHR = getVitalData(patientIds);
        index = 0;
        AlertDefinitionDTO alertDefinitionDTO = sendAlertDefinitionToKafka();
        alertDefinitionDTOKafkaProducer.sendObjectToKafka(alertDefinitionDTO, appConfig.getAlertDefinitionKafkaTopic());

    }

    @Scheduled(fixedRate = 1000)
    public void generateVitalEvents() {
        patientIds.forEach(patientId -> {
            log.debug("Pushing Vital Event in Kafka Queue for patient id:: {}", patientId);
            vitalDTOKafkaProducer.sendObjectToKafka(dataHR.get(patientId).get(index), appConfig.getVitalKafkaTopic());
        });
        index++;
    }

    public Map<String, List<VitalDTO>> getVitalData(List<String> patientIds) {
        Map<String, List<VitalDTO>> vitalDataset = new HashMap<>();
        LocalDateTime startTime = LocalDateTime.of(2023, 11, 1, 12, 0, 0);
        try {
            BufferedReader br = new BufferedReader(new FileReader("./dataset/results/results_138022_hr/xg_results.csv"));
//            BufferedReader br = new BufferedReader(new FileReader("./dataset/orig_cropped.csv"));
            br.readLine();
            String line;

            int count = 0;
            while ((line = br.readLine()) != null && count++ < 10000) {
                String[] values = line.split(",");
                String patientId = values[0];
                long seconds = (long) (Float.parseFloat(values[1]));

                double heartRateValue = Double.parseDouble(values[2]);
                heartRateValue = Math.round(heartRateValue);
                double respRateValue = Double.parseDouble(values[3]);
                respRateValue = Math.round(respRateValue);

                String time = startTime.plusSeconds(seconds).format(TimeConversion.formatter);

                if (patientIds.contains(patientId)) {
                    VitalDTO vitalHRDTO = new VitalDTO().builder()
                            .time(time)
                            .patientId(patientId)
                            .vitalValue(heartRateValue)
                            .vitalName("HeartRate").build();
                    vitalHRDTO.calculateKey();
//
//                    VitalDTO vitalRRDTO = new VitalDTO().builder()
//                            .time(time)
//                            .patientId(patientId)
//                            .vitalValue(respRateValue)
//                            .vitalName("RespRate").build();
//                    vitalRRDTO.calculateKey();

                    vitalDataset.putIfAbsent(patientId, new ArrayList<>());
                    vitalDataset.get(patientId).add(vitalHRDTO);
//                    vitalDataset.get(patientId).add(vitalRRDTO);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vitalDataset;
    }

    public AlertDefinitionDTO sendAlertDefinitionToKafka() {
        AlertDefinitionDTO alertDefinitionDTO = AlertDefinitionDTO.builder()
                .patientId("141560")
                .vitalName("HeartRate")
                .alertThreshold(100.0)
                .alertOperator(">")
                .violationDuration(5).build();
        alertDefinitionDTO.calculateKey();
        String alertId = UUID.randomUUID().toString();
        alertDefinitionDTO.setAlertID(alertId);
        return alertDefinitionDTO;
    }
}
