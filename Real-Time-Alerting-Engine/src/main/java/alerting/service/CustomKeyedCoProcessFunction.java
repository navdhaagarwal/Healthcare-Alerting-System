package alerting.service;

import alerting.dto.AlertDTO;
import alerting.dto.AlertDefinitionDTO;
import alerting.dto.VitalDTO;
import alerting.flink.Window;
import alerting.util.TimeConversion;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;


@Slf4j
public class CustomKeyedCoProcessFunction extends KeyedCoProcessFunction<String, VitalDTO, AlertDefinitionDTO, AlertDTO> {

    public final long windowSize;
    public final long windowSlide;
    private transient MapState<Long, Window> windowMapState;
    private transient MapState<String, AlertDefinitionDTO> alertDefinitionDTOMapState;

    public CustomKeyedCoProcessFunction(Time size, Time slide) {
        this.windowSize = size.toMilliseconds();
        this.windowSlide = slide.toMilliseconds();
    }
    @Override
    public void open(Configuration conf) {
        windowMapState = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                "WindowMapState", Long.class, Window.class));
        alertDefinitionDTOMapState = getRuntimeContext().getMapState(new MapStateDescriptor<>(
                "AlertDefinitionMapState", String.class, AlertDefinitionDTO.class));
    }
    @Override
    public void processElement1(VitalDTO vitalDTO, KeyedCoProcessFunction<String, VitalDTO, AlertDefinitionDTO,
            AlertDTO>.Context context, Collector<AlertDTO> collector) throws Exception {
        log.debug("Watermark:: {}",
                TimeConversion.epochMilliToLocalDateTime(context.timerService().currentWatermark()));
        log.info("Vital:: {}", vitalDTO);

        Window currWindow = createWindow(context.timerService().currentWatermark());
        windowMapState.put(currWindow.getEndTime(), currWindow);
        context.timerService().registerEventTimeTimer(currWindow.getEndTime());

        // loop over all the windows and see whether the element will lie in the window
        windowMapState.values().forEach(window -> {
            long timeOfEvent = TimeConversion.stringToEpochMilli(vitalDTO.getTime());
            if (window.getStartTime() <= timeOfEvent && timeOfEvent <= window.getEndTime()) {
                window.getVitalDTOList().add(vitalDTO);
                window.getValues().add(vitalDTO.getVitalValue());
            }
        });

    }

    @Override
    public void processElement2(AlertDefinitionDTO alertDefinitionDTO, KeyedCoProcessFunction<String, VitalDTO,
            AlertDefinitionDTO, AlertDTO>.Context context, Collector<AlertDTO> collector) throws Exception {
        log.info("Processing Alert Definition");
        alertDefinitionDTOMapState.put(alertDefinitionDTO.getAlertID(), alertDefinitionDTO);
    }

    @Override
    public void onTimer(long ts, KeyedCoProcessFunction<String, VitalDTO,
            AlertDefinitionDTO, AlertDTO>.OnTimerContext context, Collector<AlertDTO> collector) throws Exception{
        long timestampOfTrigger = context.timestamp();
        log.debug("Timer triggered for window ending at :: {}",
                TimeConversion.epochMilliToLocalDateTime(timestampOfTrigger));
        Window timedOutWindow = windowMapState.get(timestampOfTrigger);
        alertDefinitionDTOMapState.values().forEach(alertDefinitionDTO -> {
            log.debug("Values are:: {}", timedOutWindow.getValues());
            boolean alert =
                    timedOutWindow.getValues().stream().allMatch(value -> value >= alertDefinitionDTO.getAlertThreshold());
            if (alert) {
                log.info("ALERT for window {} - {}",
                        TimeConversion.epochMilliToLocalDateTime(timedOutWindow.getStartTime()),
                        TimeConversion.epochMilliToLocalDateTime(timedOutWindow.getEndTime()));
                collector.collect(createAlert(alertDefinitionDTO, timedOutWindow));
            }
        });
        windowMapState.remove(timestampOfTrigger);

    }

    private Window createWindow(long timestamp) {
        long remainder = timestamp % windowSlide;
        long windowStart = timestamp - remainder;
        long windowEnd = windowStart + windowSize - 1;

        Window currWindow = Window.builder().startTime(windowStart).endTime(windowEnd)
                .vitalDTOList(new ArrayList<>()).values(new ArrayList<>()).build();

        log.debug("Window start time is :: {} and end time is :: {}",
                TimeConversion.epochMilliToLocalDateTime(windowStart),
                TimeConversion.epochMilliToLocalDateTime(windowEnd));
        return currWindow;
    }


    private AlertDTO createAlert(AlertDefinitionDTO alertDefinitionDTO, Window window) {
        AlertDTO alertDTO = AlertDTO.builder()
                .alertID(alertDefinitionDTO.getAlertID())
                .alertOperator(alertDefinitionDTO.getAlertOperator())
                .alertThreshold(alertDefinitionDTO.getAlertThreshold())
                .patientId(alertDefinitionDTO.getPatientId())
                .vitalName(alertDefinitionDTO.getVitalName())
                .violationDuration(alertDefinitionDTO.getViolationDuration())
                .startTime(window.getStartTime())
                .endTime(window.getEndTime())
                .build();
        return alertDTO;
    }

}
