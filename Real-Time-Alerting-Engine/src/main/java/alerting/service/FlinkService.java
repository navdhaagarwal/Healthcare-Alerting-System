package alerting.service;

import alerting.config.AppConfig;
import alerting.dto.AlertDTO;
import alerting.dto.AlertDefinitionDTO;
import alerting.dto.VitalDTO;
import alerting.serde.AlertEventDeserializationSchema;
import alerting.serde.AlertSerializationSchema;
import alerting.serde.VitalEventDeserializationSchema;
import lombok.RequiredArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlinkService {

    private final AppConfig appConfig;
    private final VitalEventDeserializationSchema vitalEventDeserializationSchema;
    private final AlertEventDeserializationSchema alertEventDeserializationSchema;
    private final CustomWatermarkStrategy customWatermarkStrategy;
    private final CustomWatermarkStrategyAlert customWatermarkStrategyAlert;
    private final AlertSerializationSchema alertSerializationSchema;
    public void processAlerts() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<VitalDTO> vitalSource = KafkaSource.<VitalDTO>builder()
                .setBootstrapServers(appConfig.getKafkaBootstrapServers())
                .setTopics(appConfig.getVitalKafkaTopic())
                .setGroupId(appConfig.getKafkaGroupId())
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(vitalEventDeserializationSchema)
                .build();

        KafkaSource<AlertDefinitionDTO> alertSource = KafkaSource.<AlertDefinitionDTO>builder()
                .setBootstrapServers(appConfig.getKafkaBootstrapServers())
                .setTopics(appConfig.getAlertDefinitionKafkaTopic())
                .setGroupId(appConfig.getKafkaGroupId())
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(alertEventDeserializationSchema)
                .build();

        DataStream<VitalDTO> vitalDTODataStream = env.fromSource(vitalSource, customWatermarkStrategy,
                "Vital Kafka Source", TypeInformation.of(VitalDTO.class));

        DataStream<AlertDefinitionDTO> alertDTODataStream = env.fromSource(alertSource, customWatermarkStrategyAlert,
                "Alert Kafka Source", TypeInformation.of(AlertDefinitionDTO.class));

        ConnectedStreams<VitalDTO, AlertDefinitionDTO> connectedStreams = vitalDTODataStream.connect(alertDTODataStream)
                        .keyBy(VitalDTO::getKey, AlertDefinitionDTO::getKey);



//        connectedStreams.process(new CustomKeyedCoProcessFunction(Time.seconds(5), Time.seconds(1))).print();
        connectedStreams.process(new CustomKeyedCoProcessFunction(Time.seconds(5), Time.seconds(1))).sinkTo(kafkaSink());

        env.execute();
    }


    private KafkaSink<AlertDTO> kafkaSink() {
        KafkaRecordSerializationSchema<AlertDTO> kafkaRecordSerializationSchema =
                KafkaRecordSerializationSchema.builder().setTopic(appConfig.getAlertProducerKafkaTopic())
                        .setValueSerializationSchema(alertSerializationSchema).build();

        return KafkaSink.<AlertDTO>builder()
                .setBootstrapServers(appConfig.getKafkaBootstrapServers())
                .setRecordSerializer(kafkaRecordSerializationSchema)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }


}
