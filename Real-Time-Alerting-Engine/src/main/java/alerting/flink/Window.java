package alerting.flink;

import alerting.dto.VitalDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Window {
    private long startTime;
    private long endTime;
    private List<VitalDTO> vitalDTOList;
    private List<Double> values;
}
