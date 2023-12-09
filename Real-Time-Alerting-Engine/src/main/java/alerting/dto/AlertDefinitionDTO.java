package alerting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlertDefinitionDTO {

    @JsonProperty("alert_id")
    private String alertID;

    @JsonProperty("patient_id")
    private String patientId;

    @JsonProperty("vital_name")
    private String vitalName;

    @JsonProperty("alert_threshold")
    private Double alertThreshold;

    @JsonProperty("alert_operator")
    private String alertOperator;

    @JsonProperty("violation_duration")
    private int violationDuration;

    @JsonProperty("key")
    private String key;

    public void calculateKey() {
        this.key = String.format("patient_id=%s#vital_name=%s", this.patientId, this.vitalName);
    }


}
