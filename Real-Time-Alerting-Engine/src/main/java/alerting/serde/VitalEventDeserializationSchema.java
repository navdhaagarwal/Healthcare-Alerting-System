package alerting.serde;

import alerting.dto.VitalDTO;
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
public class VitalEventDeserializationSchema implements DeserializationSchema<VitalDTO> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public VitalDTO deserialize(byte[] data) throws IOException {
        try {
            if (data == null) {
                log.info("Null data received when deserializing");
                return new VitalDTO();
            }
            log.debug("Deserializing...");
            VitalDTO vitalDTO = objectMapper.readValue(new String(data, StandardCharsets.UTF_8), VitalDTO.class);
            log.debug("Vital Event Received :: {}", vitalDTO);
            return vitalDTO;
        } catch (Exception ex) {
            log.error("Error when deserializing vital event object ", ex);
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(VitalDTO vitalDTO) {
        return false;
    }

    @Override
    public TypeInformation<VitalDTO> getProducedType() {
        return null;
    }
}
