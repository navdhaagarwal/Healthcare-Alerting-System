package alerting.kafka;

import alerting.config.AppConfig;
import alerting.serde.KafkaEventDTOSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaProducer<T> {

    @Autowired
    private KafkaEventDTOSerializer<T> kafkaEventDTOSerializer;
    @Autowired
    private AppConfig appConfig;

    private KafkaTemplate<String, T> kafkaTemplate;


    @PostConstruct
    public void init() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaBootstrapServers());
        ProducerFactory<String, T> producerFactory = new DefaultKafkaProducerFactory<>(configProps,
                new StringSerializer(), kafkaEventDTOSerializer);
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    public void sendObjectToKafka(T message, String topicName) {
        kafkaTemplate.send(topicName, message);
    }
}
