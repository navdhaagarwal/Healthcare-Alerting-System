package alerting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties
@Component
@Data
public class AppConfig {
    private String kafkaBootstrapServers;
    private String kafkaGroupId;
    private String kafkaOffset;
    private String vitalKafkaTopic;
    private String alertDefinitionKafkaTopic;
    private String alertProducerKafkaTopic;

}
