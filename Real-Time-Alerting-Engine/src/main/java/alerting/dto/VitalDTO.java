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
public class VitalDTO {

    @JsonProperty("time_id")
    private String time;

    @JsonProperty("patient_id")
    private String patientId;

    @JsonProperty("vital_name")
    private String vitalName;

    @JsonProperty("vital_value")
    private Double vitalValue;

    @JsonProperty("key")
    private String key;

    public void calculateKey() {
        this.key = String.format("patient_id=%s#vital_name=%s", this.patientId, this.vitalName);
    }

    @Override
    public String toString() {
        return "(" +
                "time='" + time + '\'' +
                ", patientId='" + patientId + '\'' +
                ", vitalName='" + vitalName + '\'' +
                ", vitalValue=" + vitalValue +
                ')';
    }
}
