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
public class AlertDTO {

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

    @JsonProperty("alert_start_time")
    private long startTime;

    @JsonProperty("alert_end_time")
    private long endTime;

    @JsonProperty("violation_duration")
    private int violationDuration;

}
