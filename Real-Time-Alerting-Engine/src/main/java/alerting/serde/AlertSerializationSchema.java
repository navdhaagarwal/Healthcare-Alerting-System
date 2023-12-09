package alerting.serde;

import alerting.dto.AlertDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlertSerializationSchema implements SerializationSchema<AlertDTO> {

    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public void open(InitializationContext context) throws Exception {
        SerializationSchema.super.open(context);
    }

    @Override
    public byte[] serialize(AlertDTO alertDTO) {
        try {
            if (alertDTO == null) {
                log.info("Null object received while serializing");
                return new byte[0];
            }
            log.debug("Serializing the alert event!");
            return objectMapper.writeValueAsBytes(alertDTO);
        } catch (Exception e) {
            log.error("Exception on data serialization", e);
        }
        return new byte[0];
    }
}
