package alerting.service;

import alerting.dto.AlertDefinitionDTO;
import alerting.util.TimeConversion;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class CustomWatermarkStrategyAlert implements WatermarkStrategy<AlertDefinitionDTO> {

    long timeDifference = 0;
    @PostConstruct
    public void init() {
        LocalDateTime startTime = LocalDateTime.of(2023, 11, 1, 12, 0, 0);
        LocalDateTime currentTime = LocalDateTime.now();
        Instant startInstant = startTime.atZone(ZoneId.systemDefault()).toInstant();
        Instant currentInstant = currentTime.atZone(ZoneId.systemDefault()).toInstant();
        Duration duration = Duration.between(startInstant, currentInstant);
        timeDifference = duration.toMillis();
    }
    @Override
    public WatermarkGenerator<AlertDefinitionDTO> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        class CustomWatermarkGenerator implements WatermarkGenerator<AlertDefinitionDTO> {

            @Override
            public void onEvent(AlertDefinitionDTO alertDefinitionDTO, long l, WatermarkOutput watermarkOutput) {

            }

            @Override
            public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                long currentTime = System.currentTimeMillis();
                watermarkOutput.emitWatermark(new Watermark(currentTime - timeDifference));
            }
        }
        return new CustomWatermarkGenerator();
    }
}
