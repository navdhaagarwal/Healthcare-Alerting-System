package alerting.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventDTOSerializer<T> implements Serializer<T> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public byte[] serialize(String s, T kafkaEventDTO) {
        try {
            if (kafkaEventDTO == null) {
                log.info("Null object received while serializing");
                return new byte[0];
            }
            log.debug("Serializing the kafka event!");
            return objectMapper.writeValueAsBytes(kafkaEventDTO);
        } catch (Exception e) {
            log.error("Exception on data serialization", e);
        }
        return new byte[0];
    }
}
