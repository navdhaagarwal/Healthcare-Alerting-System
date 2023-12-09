package alerting.serde;

import alerting.dto.AlertDefinitionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AlertEventDeserializationSchema implements DeserializationSchema<AlertDefinitionDTO> {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public AlertDefinitionDTO deserialize(byte[] data) throws IOException {
        try {
            if (data == null) {
                log.info("Null data received when deserializing");
                return new AlertDefinitionDTO();
            }
            log.debug("Deserializing...");
            AlertDefinitionDTO alertDefinitionDTO = objectMapper.readValue(new String(data, StandardCharsets.UTF_8),
                    AlertDefinitionDTO.class);
            log.info("Alert Definition Event Received :: {}", alertDefinitionDTO);
            return alertDefinitionDTO;
        } catch (Exception ex) {
            log.error("Error when deserializing vital event object ", ex);
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(AlertDefinitionDTO alertDefinitionDTO) {
        return false;
    }

    @Override
    public TypeInformation<AlertDefinitionDTO> getProducedType() {
        return null;
    }
}
