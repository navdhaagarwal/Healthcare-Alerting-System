package alerting.service;

import alerting.dto.VitalDTO;
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
public class CustomWatermarkStrategy implements WatermarkStrategy<VitalDTO> {

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
    public WatermarkGenerator<VitalDTO> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context) {
        class CustomWatermarkGenerator implements WatermarkGenerator<VitalDTO> {

            @Override
            public void onEvent(VitalDTO vitalDTO, long l, WatermarkOutput watermarkOutput) {

            }

            @Override
            public void onPeriodicEmit(WatermarkOutput watermarkOutput) {
                long currentTime = System.currentTimeMillis();
                watermarkOutput.emitWatermark(new Watermark(currentTime - timeDifference));
            }
        }
        return new CustomWatermarkGenerator();
    }

    @Override
    public TimestampAssigner<VitalDTO> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
        class CustomTimestampAssigner implements TimestampAssigner<VitalDTO> {

            @Override
            public long extractTimestamp(VitalDTO vitalDTO, long l) {
                return TimeConversion.stringToEpochMilli(vitalDTO.getTime());
            }
        }
        return new CustomTimestampAssigner();
    }
}
